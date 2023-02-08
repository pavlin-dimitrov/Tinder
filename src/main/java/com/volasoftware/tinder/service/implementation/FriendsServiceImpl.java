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
import com.volasoftware.tinder.repository.LocationRepository;
import com.volasoftware.tinder.repository.RatingRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.FriendsService;
import com.volasoftware.tinder.service.contract.LocationService;
import java.security.Principal;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendsServiceImpl implements FriendsService {

  private final AccountRepository accountRepository;
  private final RatingRepository ratingRepository;
  private final LocationService locationService;
  private final AccountService accountService;
  private final LocationRepository locationRepository;
  private final ModelMapper modelMapper;

  @Override
  public ResponseDTO linkingAllRealAccountsWithRandomFriends() {
    List<Account> accounts = accountRepository.findAll();
    List<Account> realAccounts = accountRepository.findAllByType(AccountType.REAL);
    for (Account account : realAccounts) {
      Set<Account> friends = new HashSet<>();
      seedFriends(accounts, account, friends);
      account.setFriends(friends);
      accountRepository.save(account);
    }
    return getResponseDTO(accounts);
  }

  @Override
  public ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id) {
    List<Account> accounts = accountRepository.findAll();
    Account account = accountService.getAccountByIdIfExists(id);
    Set<Account> friends = new HashSet<>();
    seedFriends(accounts, account, friends);
    account.setFriends(friends);
    accountRepository.save(account);
    return getResponseDTO(accounts);
  }

  @Override
  public List<FriendDTO> showAllMyFriends(Principal principal, LocationDTO myLocation) {
    Account account = accountService.getAccountByEmailIfExists(principal.getName());
    if (myLocation != null) {
      Location location = new Location();
      location.setAccount(account);
      location.setLatitude(myLocation.getLatitude());
      location.setLongitude(myLocation.getLongitude());
      locationRepository.save(location);

      account.setLocation(location);
      accountRepository.save(account);

      return getListOfFriendsDTOsOrderedByDistance(myLocation, account);
    }
    return getListOfFriendsDTOsNotOrderedByDistance(account);
  }

  @Override
  public void checkIfUsersAreFriends(Account account, Account friend) {
    if (!account.getFriends().contains(friend)) {
      log.warn("You are not friend with this user, cannot rate it!");
      throw new MissingFriendshipException("You are not friend with this user, cannot rate it!");
    }
  }

  @Override
  public List<FriendDTO> showFilteredListOfFriends(
      Principal principal, LocationDTO locationDTO, Integer limit) {
    Account account = accountService.getAccountByEmailIfExists(principal.getName());
    return getListOfFriendsFilteredByRatingOrderedDesc(account, limit);
  }

  @Override
  public AccountDTO getFriendInfo(String email, Long userId) {
    Account principalAccount = accountService.getAccountByEmailIfExists(email);
    Account user = accountService.getAccountByIdIfExists(userId);
    checkIfUsersAreFriends(principalAccount, user);
    return modelMapper.map(user, AccountDTO.class);
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

  private List<FriendDTO> getListOfFriendsDTOsOrderedByDistance(
      LocationDTO myLocation, Account account) {
    return account.getFriends().stream()
        .map(friend -> modelMapper.map(friend, FriendDTO.class))
        .sorted(Comparator.comparingDouble(friend -> locationService.getFriendDistance(myLocation, friend.getLocationDTO())))
        .collect(Collectors.toList());
  }

  private List<FriendDTO> getListOfFriendsDTOsNotOrderedByDistance(Account account) {
    return account.getFriends().stream()
        .map(friend -> modelMapper.map(friend, FriendDTO.class))
        .collect(Collectors.toList());
  }

  private List<FriendDTO> getListOfFriendsFilteredByRatingOrderedDesc(Account account, Integer limit) {
    if (limit == null) {
      limit = Integer.MAX_VALUE;
    }
    List<Rating> ratings = ratingRepository.findAllByAccount(account);
    return ratings.stream()
        .sorted(Comparator.comparingInt(Rating::getRating).reversed())
        .map(rating -> modelMapper.map(rating.getFriend(), FriendDTO.class))
        .limit(limit)
        .collect(Collectors.toList());
  }
}
