package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.dto.FriendDto;
import com.volasoftware.tinder.dto.LocationDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Rating;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.exception.AccountIsNotRealException;
import com.volasoftware.tinder.exception.MissingFriendshipException;
import com.volasoftware.tinder.exception.OriginGreaterThenBoundException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.mapper.FriendMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendService;
import com.volasoftware.tinder.service.contract.LocationService;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.transaction.Transactional;

import com.volasoftware.tinder.exception.NoRealAccountsFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
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
  private static final String LOCATION = "location";
  private static final String RATING = "rating";
  private static final String DESC = "desc";

  @Override
  public List<FriendDto> findFriendsByRating(Principal principal, String direction) {
    Sort sort = Sort.by(Sort.Direction.fromString(direction), "rating");

    Long currentAccountId = accountService.getAccountByEmailIfExists(principal.getName()).getId();

    List<Long> friendIdListOrderedByRating =
        ratingRepository.findRatedFriendsByAccountId(currentAccountId, sort);

    return getAccountListById(friendIdListOrderedByRating).stream()
        .map(FriendMapper.INSTANCE::accountToFriendDto)
        .collect(Collectors.toList());
  }

  @NotNull
  private List<Account> getAccountListById(List<Long> friendIds) {
    return friendIds.stream()
        .map(accountService::getAccountByIdIfExists)
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

  @Override
  public List<FriendDto> showFilteredListOfFriends(
      String sortedBy,
      String orderedBy,
      Principal principal,
      LocationDto locationDto,
      Integer limit) {

    Account account = accountService.getAccountByEmailIfExists(principal.getName());

    List<FriendDto> friends = getFriendsList(account);
    friends = getFilteredFriends(sortedBy, orderedBy, locationDto, account, friends);

    return applyLimit(limit, friends);
  }

  private List<FriendDto> getFilteredFriends(
      String sortedBy,
      String orderedBy,
      LocationDto locationDto,
      Account account,
      List<FriendDto> friends) {
    if (sortedBy.equalsIgnoreCase(LOCATION)) {
      friends = sortFriendsByLocationAsc(locationDto, friends);
    } else if (sortedBy.equalsIgnoreCase(RATING)) {
      friends = sortFriendsByRatingAsc(account);
    }

    if (orderedBy.equalsIgnoreCase(DESC)) {
      Collections.reverse(friends);
    }

    return friends;
  }

  private List<FriendDto> applyLimit(Integer limit, List<FriendDto> friends) {
    if (limit == null || limit >= friends.size()) {
      return friends;
    }
    return friends.subList(0, limit);
  }

  private List<FriendDto> sortFriendsByRatingAsc(Account account) {
    List<Rating> ratings = ratingRepository.findAllByAccount(account);
    return ratings.stream()
        .sorted(Comparator.comparingInt(Rating::getRating))
        .map(rating -> FriendMapper.INSTANCE.accountToFriendDto(rating.getFriend()))
        .collect(Collectors.toList());
  }

  private List<FriendDto> sortFriendsByLocationAsc(
      LocationDto locationDto, List<FriendDto> friends) {
    return friends.stream()
        .sorted(
            Comparator.comparingDouble(
                friend -> locationService.getFriendDistance(locationDto, friend.getLocationDto())))
        .collect(Collectors.toList());
  }

  private List<FriendDto> getFriendsList(Account account) {
    return account.getFriends().stream()
        .map(FriendMapper.INSTANCE::accountToFriendDto)
        .collect(Collectors.toList());
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
