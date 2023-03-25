package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.dto.FriendDto;
import com.volasoftware.tinder.dto.LocationDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.exception.AccountIsNotRealException;
import com.volasoftware.tinder.exception.MissingFriendshipException;
import com.volasoftware.tinder.exception.NoRealAccountsFoundException;
import com.volasoftware.tinder.exception.OriginGreaterThenBoundException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendService;
import com.volasoftware.tinder.service.contract.LocationService;
import java.security.Principal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendServiceImpl implements FriendService {

  private final AccountRepository accountRepository;
  private final RatingRepository ratingRepository;
  private final LocationService locationService;
  private final AccountService accountService;
  private static final String RATING = "rating";

  @Override
  public List<FriendDto> showFilteredListOfFriends(String sortedBy, String orderedBy, Principal principal,
      LocationDto locationDto, Integer limit) {

    List<FriendDto> filteredListOfFriends =
        filterFriendsByLocation(locationDto, principal, orderedBy, limit);

    if (sortedBy.equalsIgnoreCase(RATING)) {
      filteredListOfFriends = filterFriendsByRating(principal, orderedBy, limit);
    }
    return filteredListOfFriends;
  }

  private List<FriendDto> filterFriendsByRating(Principal principal, String direction, int limit) {
    limit = setNoLimit(limit);

    Pageable pageable = PageRequest.of(0, limit, Sort.Direction.fromString(direction), RATING);

    Long currentAccountId = accountService.getAccountByEmailIfExists(principal.getName()).getId();

    List<Account> friendIdListOrderedByRating =
        ratingRepository.findRatedFriendsByAccountId(currentAccountId, pageable);

    return friendIdListOrderedByRating.stream()
        .map(FriendMapper.INSTANCE::accountToFriendDto)
        .collect(Collectors.toList());
  }

  private List<FriendDto> filterFriendsByLocation(
      LocationDto locationDto, Principal principal, String direction, int limit) {
    limit = setNoLimit(limit);

    Long currentAccountId = accountService.getAccountByEmailIfExists(principal.getName()).getId();

    List<Account> listOfFriendsWithLocations =
        accountRepository.findFriendsWithLocationsByAccountId(currentAccountId);

    Comparator<FriendDto> comparedFriendsDistances =
        Comparator.comparingDouble(
            friend -> locationService.getFriendDistance(locationDto, friend.getLocationDto()));

    if (Sort.Direction.fromString(direction) == Direction.DESC) {
      comparedFriendsDistances = comparedFriendsDistances.reversed();
    }

    return listOfFriendsWithLocations.stream()
        .map(FriendMapper.INSTANCE::accountToFriendDto)
        .sorted(comparedFriendsDistances)
        .limit(limit)
        .collect(Collectors.toList());
  }

  @Override
  public AccountDto getFriendInfo(String email, Long userId) {
    Account user = accountService.getAccountByEmailIfExists(email);
    Account friend = accountService.getAccountByIdIfExists(userId);

    checkIfUsersAreFriends(user, friend);

    return AccountMapper.INSTANCE.mapAccountToAccountDto(friend);
  }

  @Override
  public ResponseDto linkingAllRealAccountsWithRandomFriends() {
    log.info("Seed friends for all REAL accounts.");
    List<Account> realAccounts = accountRepository.findAllByType(AccountType.REAL);

    if (realAccounts.isEmpty()) {
      throw new NoRealAccountsFoundException();
    }

    List<Account> botAccounts = accountRepository.findAllByType(AccountType.BOT);
    boolean isSeedingSuccessful = true;

    for (Account account : realAccounts) {
      if (account.getFriends() != null && !account.getFriends().isEmpty()) {
        log.info("Account " + account.getEmail() + " already has friends. Skipping...");
        continue;
      }

      Set<Account> friends = seedFriends(botAccounts, account);

      account.setFriends(friends);

      accountRepository.save(account);
      log.info("Save set of Friends for: " + account.getEmail());

      if (friends.isEmpty()) {
        isSeedingSuccessful = false;
      }
    }

    return getResponseDto(isSeedingSuccessful, "Some or all accounts already had friends.");
  }

  @Override
  public ResponseDto linkingRequestedRealAccountWithRandomFriends(Long id) {
    Account currentAccount = accountService.getAccountByIdIfExists(id);

    if (!isRealCurrentAccount(currentAccount)) {
      throw new AccountIsNotRealException();
    }

    if (currentAccount.getFriends() != null && !currentAccount.getFriends().isEmpty()) {
      return getResponseDto(false, "Current account already has a set of friends.");
    }

    List<Account> botAccounts = accountRepository.findAllByType(AccountType.BOT);
    Set<Account> friends = seedFriends(botAccounts, currentAccount);

    currentAccount.setFriends(friends);

    accountRepository.save(currentAccount);

    return getResponseDto(!friends.isEmpty(), "Seeding friends was not successful.");
  }

  @Override
  @Transactional
  @Async("threadPoolTaskExecutor")
  public void linkFriendsAsync(Long id) {
    try {
      Thread.sleep(5000);
      linkingRequestedRealAccountWithRandomFriends(id);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void checkIfUsersAreFriends(Account account, Account friend) {
    if (!account.getFriends().contains(friend)) {
      log.warn("You are not friend with this user, cannot rate it!");
      throw new MissingFriendshipException();
    }
  }

  private int setNoLimit(int limit) {
    if (limit == -1) {
      limit = Integer.MAX_VALUE;
    }
    return limit;
  }

  private Set<Account> seedFriends(List<Account> botAccounts, Account account) {
    Set<Account> friends = new HashSet<>();
    for (int i = 0; i < botAccounts.size(); i++) {
      Account friend = botAccounts.get(getRandomFriendId(botAccounts.size()));
      if (isNotSameAccount(account, friend) && isBotAccount(friend)) {
        friends.add(friend);
      }
    }
    return friends;
  }

  private int getRandomFriendId(int bound) {
    int origin = 0;
    if (bound == origin) {
      throw new OriginGreaterThenBoundException();
    }
    return ThreadLocalRandom.current().nextInt(origin, bound);
  }

  private ResponseDto getResponseDto(boolean isSuccessful, String failureMessage) {
    ResponseDto response = new ResponseDto();
    String message = isSuccessful ? "Friends seeded successfully!" : failureMessage;
    response.setResponse(message);
    return response;
  }

  private boolean isRealCurrentAccount(Account account) {
    return account.getType().equals(AccountType.REAL);
  }

  private boolean isNotSameAccount(Account friend, Account account) {
    return !friend.getId().equals(account.getId());
  }

  private boolean isBotAccount(Account friend) {
    return friend.getType() == AccountType.BOT;
  }
}
