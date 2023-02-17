package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.auth.ErrorResponse;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.service.contract.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.security.Principal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("api/v1/accounts")
@Api(value = "Account controller")
public class AccountController {

  private final AccountService accountService;

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

  @ApiOperation(value = "Edit personal account", response = AccountDTO.class)
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Account updated successfully"),
        @ApiResponse(code = 401, message = "You are not authorized to edit this account"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource is not found")
      })
  @PutMapping("/profile")
  public ResponseEntity<AccountDTO> editAccountInfo(
      @ApiParam(value = "Account information to update", required = true) @RequestBody
          AccountDTO accountDTO,
      @ApiParam(value = "The authenticated user", required = true) Principal principal)
      throws NotAuthorizedException {
    AccountDTO updatedAccountDto = accountService.updateAccountInfo(accountDTO, principal);
    return new ResponseEntity<>(updatedAccountDto, HttpStatus.OK);
  }

  @ApiOperation(value = "Retrieves an account profile", response = AccountDTO.class)
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Successfully retrieved account"),
          @ApiResponse(code = 401, message = "You are not authorized to view the resource"),
          @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @GetMapping("profile")
  public ResponseEntity<AccountDTO> showUserProfile(
      @ApiParam(value = "Id of account to be shown", required = true) @RequestParam Long id) {
    return new ResponseEntity<>(accountService.findAccountById(id), HttpStatus.OK);
  }
}
