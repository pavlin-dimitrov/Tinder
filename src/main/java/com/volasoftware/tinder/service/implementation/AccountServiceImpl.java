package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

  @Autowired private final AccountRepository accountRepository;

  public List<Account> getAccounts() {
    return accountRepository.findAll();
  }

  public Account addNewAccount(Account account) {
    Optional<Account> accountByEmail = accountRepository.findAccountByEmail(account.getEmail());
    if (accountByEmail.isPresent()) {
      throw new IllegalStateException("Email is taken! Use another e-mail address!");
    }
    return accountRepository.save(account);
  }
}
