package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.service.contract.AccountService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class AccountController {

  @Autowired private final AccountService accountService;

  @GetMapping("/accounts")
  public List<Account> allAccounts() {
    return accountService.getAccounts();
  }

  @PostMapping("/add-account")
  public void newAccount(@RequestBody Account newAccount) {
    Account create = accountService.addNewAccount(newAccount);
  }
}
