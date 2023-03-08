package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import com.volasoftware.tinder.service.contract.EmailVerificationService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("api/v1/verify-email")
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class VerificationController {

  private final EmailVerificationService emailVerificationService;
  private final VerificationTokenRepository verificationTokenRepository;

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
  @PostMapping("/verify")
  public RedirectView verifyAccountEmail(
      @ApiParam(
              name = "token",
              value = "The token provided to verify the email address",
              required = true)
          @RequestParam("token")
          String token)
      throws AccountNotFoundException {
    log.info("Received request to verify email with token: {}", token);
    emailVerificationService.verifyEmail(token);
    return new RedirectView("/login");
  }

  @ApiOperation(value = "Re-Verification of new account e-mail address.")
  @ApiResponses(
      value = {
        @ApiResponse(code = 200, message = "Successfully verified"),
        @ApiResponse(code = 400, message = "Token expired or invalid"),
        @ApiResponse(code = 401, message = "Not authorized action"),
        @ApiResponse(code = 403, message = "Accessing the resource is forbidden"),
        @ApiResponse(code = 404, message = "The resource you were trying to reach is not found")
      })
  @PostMapping("/resend-verification-email")
  public ResponseEntity<ResponseDTO> resendVerificationEmail(
      @ApiParam(
              name = "email",
              value = "Email address for which the verification email should be sent",
              required = true)
          @RequestParam("email")
          String email)
      throws AccountNotFoundException {
    log.info("Received request to RESEND verification email");
    return new ResponseEntity<>(
        emailVerificationService.resendVerificationEmail(email), HttpStatus.OK);
  }
}
