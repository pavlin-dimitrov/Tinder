package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.service.contract.AccountService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {

  @Autowired private final AccountService accountService;

  @GetMapping
  public ResponseEntity<List<AccountDTO>> getAllAccounts() {
    List<AccountDTO> accounts = accountService.getAccounts();
    return ResponseEntity.ok(accounts);
  }

  @PostMapping("/register")
  public ResponseEntity<AccountRegisterDTO> createAccount(@RequestBody AccountRegisterDTO dto) {
    AccountRegisterDTO newAccount = accountService.addNewAccount(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
  }
}
