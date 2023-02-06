package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.Location;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.LocationRepository;
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
  private final LocationService locationService;
  private final AccountService accountService;
  private final LocationRepository locationRepository;

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

  private void seedFriends(List<Account> accounts, Account account, Set<Account> friends) {
    for (int i = 0; i < getNumberOfFriendsToSeed(accounts); i++) {
      Account friend = accounts.get(i);
      if (!friend.getId().equals(account.getId()) && friend.getType() == AccountType.BOT) {
        friends.add(friend);
      }
    }
  }

  private int getNumberOfFriendsToSeed(List<Account> accounts) {
    return ThreadLocalRandom.current().nextInt(2, accounts.size());
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
        .sorted(Comparator.comparingDouble(f -> locationService.getFriendDistance(myLocation, f.getLocationDTO())))
        .collect(Collectors.toList());
  }

  private List<FriendDTO> getListOfFriendsDTOsNotOrderedByDistance(Account account) {
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
}
