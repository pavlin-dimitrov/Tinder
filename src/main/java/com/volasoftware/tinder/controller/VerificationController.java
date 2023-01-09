package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.service.contract.EmailVerificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.security.auth.login.AccountNotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("api/v1/accounts")

@Slf4j
public class VerificationController {

  private EmailVerificationService emailVerificationService;

  @ApiOperation(value = "Verification of new account e-mail address.")
  @ApiResponses(
      value = {
          @ApiResponse(code = 200, message = "Successfully verified"),
          @ApiResponse(code = 400, message = "Token expired or invalid"),
          @ApiResponse(code = 401, message = "Not authorized action"),
          @ApiResponse(
              code = 403,
              message = "Accessing the resource you were trying to reach is forbidden"),
          @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @GetMapping("/verify")  //TODO redirect to Login page....
  public ResponseEntity<?> verifyAccountEmail(@RequestParam("token") String token)
      throws AccountNotFoundException {
    log.info("Received request to verify email with token: {}", token);
    return emailVerificationService.verifyEmail(token);
  }
}