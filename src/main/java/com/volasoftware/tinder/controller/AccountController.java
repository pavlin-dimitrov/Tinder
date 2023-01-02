package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.service.contract.AccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/accounts")
public class AccountController {

  @Autowired private final AccountService accountService;

  @ApiOperation(value = "Retrieves a list of accounts")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully retrieved list of accounts"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  @GetMapping
  public ResponseEntity<List<AccountDTO>> getAllAccounts() {
    List<AccountDTO> accounts = accountService.getAccounts();
    return ResponseEntity.ok(accounts);
  }

  @ApiOperation(value = "Create new account / registration")
  @ApiResponses(value = {
          @ApiResponse(code = 200, message = "Successfully registered new account"),
          @ApiResponse(code = 401, message = "Not authorized action"),
          @ApiResponse(code = 403, message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
  })
  @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")
  @PostMapping("/register")
  public ResponseEntity<AccountRegisterDTO> createAccount(@RequestBody AccountRegisterDTO dto) {
    AccountRegisterDTO newAccount = accountService.addNewAccount(dto);
    return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
  }
}
