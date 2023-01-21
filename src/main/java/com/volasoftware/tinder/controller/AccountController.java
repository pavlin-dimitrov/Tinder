package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.service.contract.AccountService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

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
    public ResponseEntity<ResponseDTO<AccountDTO>> editAccountInfo(@RequestBody AccountDTO accountDTO, Principal principal) {
        if (!principal.getName().equals(accountDTO.getEmail())) {
            return new ResponseEntity<>(new ResponseDTO<>(
                    "You are not authorized to edit this account", null), HttpStatus.BAD_REQUEST);
        }
        Account account = accountService.updateAccountInfo(accountDTO);
        if (account == null) {
            return new ResponseEntity<>(new ResponseDTO<>(
                    "Account is not found", null), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(new ResponseDTO<>(
                "Account updated successfully", accountDTO), HttpStatus.BAD_REQUEST);
    }
}