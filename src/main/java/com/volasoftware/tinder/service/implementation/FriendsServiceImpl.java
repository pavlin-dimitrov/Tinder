package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.FriendsService;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FriendsServiceImpl implements FriendsService {

  private final AccountRepository accountRepository;
  private final ResponseDTO response = new ResponseDTO();

  @Override
  public ResponseDTO linkingAllRealAccountsWithRandomFriends() {
    List<Account> accounts = accountRepository.findAll();
    List<Account> realAccounts =
        accounts.stream()
            .filter(account -> account.getType() == AccountType.REAL)
            .collect(Collectors.toList());

    for (Account account : realAccounts) {
      int numberOfFriends = ThreadLocalRandom.current().nextInt(2, accounts.size());
      Set<Account> friends = new HashSet<>();
      for (int i = 0; i < numberOfFriends; i++) {
        Account friend = accounts.get(i);
        if (!friend.getId().equals(account.getId()) && friend.getType() == AccountType.BOT) {
          friends.add(friend);
        }
      }
      account.setFriends(friends);
      accountRepository.save(account);
    }
    if (accounts.stream().anyMatch(a -> !a.getFriends().isEmpty())) {
      response.setResponse("All real accounts are linked to random friends");
      return response;
    } else response.setResponse("Bad request. No accounts of type REAL");
    return response;
  }

  @Override
  public ResponseDTO linkingRequestedRealAccountWithRandomFriends(Long id) {
    List<Account> accounts = accountRepository.findAll();
    Optional<Account> optionalAccount = accountRepository.findById(id);

    if (optionalAccount.isPresent() && optionalAccount.get().getType() == AccountType.REAL) {
      Account account = optionalAccount.get();
      int numberOfFriends = ThreadLocalRandom.current().nextInt(0, accounts.size());
      Set<Account> friends = new HashSet<>();
      for (int i = 0; i < numberOfFriends; i++) {
        Account friend = accounts.get(i);
        if (!friend.getId().equals(account.getId()) && friend.getType() == AccountType.BOT) {
          friends.add(friend);
        }
      }
      account.setFriends(friends);
      accountRepository.save(account);
      response.setResponse("Friends seeded successfully!");
      return response;
    }
    response.setResponse("Invalid account id or account is not of type REAL");
    return response;
  }
}
