package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.entity.Rating;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.exception.MissingFriendshipException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendsService;
import com.volasoftware.tinder.service.contract.LocationService;
import java.security.Principal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendsServiceImpl implements FriendsService {

  private final AccountRepository accountRepository;
  private final RatingRepository ratingRepository;
  private final LocationService locationService;
  private final AccountService accountService;
  private final ModelMapper modelMapper;
  private final static String location = "location";
  private final static String rating = "rating";
  private final static String asc = "asc";
  private final static String desc = "desc";

  @Override
  public AccountDTO getFriendInfo(String email, Long userId) {
    Account user = accountService.getAccountByEmailIfExists(email);
    Account friend = accountService.getAccountByIdIfExists(userId);
    checkIfUsersAreFriends(user, friend);
    log.info("Check if the users are friends and if they are, return info.");
    return modelMapper.map(user, AccountDTO.class);
  }

  @Override
  public ResponseDTO linkingAllRealAccountsWithRandomFriends() {
    log.info("Seed friends for all REAL accounts.");
    List<Account> accounts = accountRepository.findAll();
    List<Account> realAccounts = accountRepository.findAllByType(AccountType.REAL);
    for (Account account : realAccounts) {
      Set<Account> friends = new HashSet<>();
      seedFriends(accounts, account, friends);
      account.setFriends(friends);
      accountRepository.save(account);
      log.info("Save set of Friends for: " + account.getEmail());
    }
    return getResponseDTO(accounts);
  }

  @Override
  public ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id) {
    log.info("Seed friends for requested REAL account.");
    List<Account> accounts = accountRepository.findAll();
    Account account = accountService.getAccountByIdIfExists(id);
    Set<Account> friends = new HashSet<>();
    seedFriends(accounts, account, friends);
    account.setFriends(friends);
    accountRepository.save(account);
    log.info("Save set of Friends for: " + account.getEmail());
    return getResponseDTO(accounts);
  }

  @Override
  @Transactional
  @Async("threadPoolTaskExecutor")
  public void linkFriendsAsync(Long id){
    try {
      Thread.sleep(20000);
      linkingRequestedRealAccountWithRandomFriends(id);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void checkIfUsersAreFriends(Account account, Account friend) {
    if (!account.getFriends().contains(friend)) {
      log.warn("You are not friend with this user, cannot rate it!");
      throw new MissingFriendshipException("You are not friend with this user, cannot rate it!");
    }
  }

  @Override
  public List<FriendDTO> showFilteredListOfFriends(String sortedBy, String orderedBy,
      Principal principal, LocationDTO locationDTO, Integer limit) {

    Account account = accountService.getAccountByEmailIfExists(principal.getName());
    log.info("Get account: " + account.getEmail());
    List<FriendDTO> friends = getFriendsList(account);
    log.info("Get list of friends. List size: " + friends.size());

    if (sortedBy.equalsIgnoreCase(location)) {
      friends = sortFriendsByLocation(locationDTO, friends);
      log.info("List, sorted by location in ASC order.");
    } else if (sortedBy.equalsIgnoreCase(rating)) {
      friends = sortFriendsByRating(account);
      log.info("List, sorted by rating in ASC order.");
    }

    if (orderedBy.equalsIgnoreCase(asc)) {
      log.info("Filtered list ordered by ASC with limit: ");
      return getLimitedListOfFriends(limit, friends);
    } else if (orderedBy.equalsIgnoreCase(desc)) {
      friends = orderFriendsDescending(account, friends, sortedBy, locationDTO);
      log.info("Filtered list ordered by ASC with limit: " + limit);
      return getLimitedListOfFriends(limit, friends);
    }
    return friends;
  }

  private List<FriendDTO> orderFriendsDescending(
      Account account, List<FriendDTO> friends, String sortedBy, LocationDTO locationDTO) {
    List<Rating> ratings = ratingRepository.findAllByAccount(account);
    List<FriendDTO> sortedFriends;
    if (sortedBy.equals("location")) {
      sortedFriends =
          friends.stream()
              .sorted(
                  Comparator.comparingDouble(
                      friend ->
                          locationService.getFriendDistance(locationDTO, friend.getLocationDTO())))
              .collect(Collectors.toList());
    } else if (sortedBy.equals("rating")) {
      sortedFriends =
          ratings.stream()
              .sorted(Comparator.comparingInt(Rating::getRating))
              .map(rating -> modelMapper.map(rating.getFriend(), FriendDTO.class))
              .collect(Collectors.toList());
    } else {
      sortedFriends = friends;
    }
    Collections.reverse(sortedFriends);
    return sortedFriends;
  }

  private List<FriendDTO> getLimitedListOfFriends(Integer limit, List<FriendDTO> friends) {
    if (limit == null || limit >= friends.size()) {
      return friends;
    }
    return friends.subList(0, limit);
  }

  private List<FriendDTO> sortFriendsByRating(Account account) {
    List<Rating> ratings = ratingRepository.findAllByAccount(account);
    return ratings.stream()
        .sorted(Comparator.comparingInt(Rating::getRating))
        .map(rating -> modelMapper.map(rating.getFriend(), FriendDTO.class))
        .collect(Collectors.toList());
  }

  private List<FriendDTO> sortFriendsByLocation(LocationDTO locationDTO, List<FriendDTO> friends) {
    return friends.stream()
        .sorted(
            Comparator.comparingDouble(
                friend -> locationService.getFriendDistance(locationDTO, friend.getLocationDTO())))
        .collect(Collectors.toList());
  }

  private List<FriendDTO> getFriendsList(Account account) {
    return account.getFriends().stream()
        .map(
            friend -> {
              Location friendLocation = friend.getLocation();
              LocationDTO friendLocationDTO =
                  new LocationDTO(friendLocation.getLatitude(), friendLocation.getLongitude());
              return new FriendDTO(
                  friend.getFirstName(),
                  friend.getLastName(),
                  friend.getImage(),
                  friend.getGender(),
                  friend.getAge(),
                  friendLocationDTO);
            })
        .collect(Collectors.toList());
  }

  private void seedFriends(List<Account> accounts, Account account, Set<Account> friends) {
    for (int i = 0; i < accounts.size(); i++) {
      Account friend = accounts.get(getRandomFriendId());
      if (!friend.getId().equals(account.getId()) && friend.getType() == AccountType.BOT) {
        friends.add(friend);
      }
    }
  }

  private int getRandomFriendId() {
    return ThreadLocalRandom.current().nextInt(2, 20);
  }

  private ResponseDTO getResponseDTO(List<Account> accounts) {
    ResponseDTO response = new ResponseDTO();
    if (accounts.stream().anyMatch(a -> !a.getFriends().isEmpty())) {
      response.setResponse("Friends seeded successfully!");
      return response;
    } else response.setResponse("Invalid account id or account is not of type REAL");
    return response;
  }
}
