package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("api/v1/accounts")
@Api(value = "Account controller")
public class AccountController {

  private final AccountService accountService;
  private final AuthenticationService authenticationService;

  @ApiOperation(value = "Retrieves a list of accounts", response = AccountDTO.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully retrieved list of accounts"),
        @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @GetMapping
  public ResponseEntity<List<AccountDTO>> getAllAccounts() {
    log.info("Received request to get all accounts");
    return ResponseEntity.ok(accountService.getAccounts());
  }

  @ApiOperation(value = "Create new account / registration")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully registered new account"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(
            code = 403,
            message = "Accessing the resource you were trying to reach is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @PostMapping("/register")
  public ResponseEntity<AccountRegisterDTO> createAccount(@RequestBody AccountRegisterDTO dto) {
    log.info("Received request to register new account with e-mail: " + dto.getEmail());
    //accountService.addNewAccount(dto)
    return ResponseEntity.status(HttpStatus.CREATED).body(authenticationService.register(dto));
  }
}