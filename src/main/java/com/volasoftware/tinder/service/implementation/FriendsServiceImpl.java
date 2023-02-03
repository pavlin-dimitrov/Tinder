package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.FriendDTO;
import com.volasoftware.tinder.DTO.LocationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.FriendsService;
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
    Account account = accountRepository.findById(id).orElseThrow(() -> new AccountNotFoundException("Account not found!"));
    Set<Account> friends = new HashSet<>();
    seedFriends(accounts, account, friends);
    account.setFriends(friends);
    accountRepository.save(account);
    return getResponseDTO(accounts);
  }

  @Override
  public List<FriendDTO> showAllMyFriendsOrderedByClosestLocation(Principal principal) {
    Account account =
        accountRepository
            .findAccountByEmail(principal.getName())
            .orElseThrow(() -> new AccountNotFoundException("This account is not present!"));
    LocationDTO myLocation = modelMapper.map(account.getLocation(), LocationDTO.class);

    return account.getFriends().stream()
        .map(friend -> new FriendDTO(friend.getFirstName(), friend.getLastName(),
            friend.getImage(), friend.getGender(),
            friend.getAge(), friend.getLocation()))
        .sorted(Comparator.comparingDouble(f -> distance(myLocation, modelMapper.map(f.getLocation(), LocationDTO.class))))
        .collect(Collectors.toList());
  }

  private static double distance(LocationDTO myLocation, LocationDTO friendLocation) {
    final int R = 6371;
    double latDistance = Math.toRadians(friendLocation.getLatitude() - myLocation.getLatitude());
    double lonDistance = Math.toRadians(friendLocation.getLongitude() - myLocation.getLongitude());
    double a =
        Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(myLocation.getLatitude()))
                * Math.cos(Math.toRadians(friendLocation.getLatitude()))
                * Math.sin(lonDistance / 2)
                * Math.sin(lonDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = R * c * 1000;
    distance = Math.pow(distance, 2);
    return Math.sqrt(distance);
  }

  private void seedFriends(List<Account> accounts, Account account, Set<Account> friends) {
    for (int i = 0; i < getNumberOfFriendsToSeed(accounts); i++) {
      Account friend = accounts.get(i);
      if (!friend.getId().equals(account.getId()) && friend.getType() == AccountType.BOT) {
        friends.add(friend);
      }
    }
  }

  private int getNumberOfFriendsToSeed( List<Account> accounts ) {
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
}
