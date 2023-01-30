package com.volasoftware.tinder.service.implementation;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.auth.AuthenticationResponse;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.AccountNotVerifiedException;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.exception.MissingRefreshTokenException;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.JwtService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthenticationServiceImpl implements AuthenticationService {
  private final VerificationTokenService verificationTokenService;
  private final AuthenticationManager authenticationManager;
  private final AccountService accountService;
  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;
  private final JwtService jwtService;
  private final EmailService emailService;

  @Override
  public ResponseDTO register(AccountRegisterDTO accountRegisterDTO) {
    log.info("Register new account with email {}", accountRegisterDTO.getEmail());
    Optional<Account> accountByEmail =
        accountService.findAccountByEmail(accountRegisterDTO.getEmail());
    ResponseDTO response = new ResponseDTO();
    if (accountByEmail.isPresent()) {
      throw new EmailIsTakenException("Email is taken! Use another e-mail address!");
    }
    Account account = modelMapper.map(accountRegisterDTO, Account.class);
    account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));
    account.setRole(Role.USER);
    account = accountService.saveAccount(account);
    VerificationToken token = verificationTokenService.createVerificationToken(account);
    log.info("Verification token generated for email: {}", accountRegisterDTO.getEmail());
    try {
      emailService.sendVerificationEmail(accountRegisterDTO.getEmail(), token.getToken());
      log.info("Email with verification token sent to email: {}", accountRegisterDTO.getEmail());
      response.setResponse("Check your e-mail to confirm the registration");
    } catch (MessagingException e) {
      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
      response.setResponse("Failed to re-send verification e-mail!");
      e.printStackTrace();
    }
    return response;
  }

  @Override
  public AuthenticationResponse login(AccountLoginDTO accountLoginDTO) {
    verifyLogin(accountLoginDTO);
    log.info("Login verified.");

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDTO.getEmail(), accountLoginDTO.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("Authentication successfully added to Security Context Holder");

    var user = accountService.findAccountByEmail(accountLoginDTO.getEmail()).orElseThrow();
    var accessToken = jwtService.generateAccessToken(user);
    log.info("Access token created after the Login.");
    var refreshToken = jwtService.generateRefreshToken(user);
    log.info("Refresh token created after Login.");

    return AuthenticationResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public void getNewPairAuthTokens(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    final String authHeader = request.getHeader("Authorization");
    final String refresh_jwt;
    final String userEmail;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      try {
        refresh_jwt = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refresh_jwt);
        Optional<Account> optionalAccount = accountService.findAccountByEmail(userEmail);
        if (optionalAccount.isEmpty()) {
          log.error("Account was not found in /refresh endpoint");
          throw new AccountNotFoundException("Account not found for the refresh token!");
        }
        Account account = optionalAccount.get();
        String access_jwt = jwtService.generateAccessToken(account);
        Map<String, String> tokens = new HashMap<>();
        tokens.put("refreshToken", refresh_jwt);
        tokens.put("accessToken", access_jwt);
        response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
      } catch (Exception e) {
        log.error("Error refreshing the access token: {}", e.getMessage());
        response.setHeader("error", e.getMessage());
        response.setStatus(FORBIDDEN.value());
        Map<String, String> error = new HashMap<>();
        error.put("error_message", e.getMessage());
        new ObjectMapper().writeValue(response.getOutputStream(), error);
      }
    } else {
      log.warn("Refresh token is missing, can not refresh the access token.");
      throw new MissingRefreshTokenException("Refresh token is missing!");
    }
  }

  @Override
  public ResponseDTO recoverPassword(Principal principal) {
    Account account =
        accountService
            .findAccountByEmail(principal.getName())
            .orElseThrow(() -> new AccountNotFoundException("Account was not found"));

    String newPassword = UUID.randomUUID().toString();
    ResponseDTO response = new ResponseDTO();

    try {
      emailService.sendPasswordRecoveryEmail(account.getEmail(), newPassword);
      response.setResponse("Check your e-mail for the new password!");
      log.info("Email with recovered password was sent to email: {}", account.getEmail());
      String encodedNewPass = passwordEncoder.encode(newPassword);
      log.info("Encode the password");
      accountService.saveNewPasswordInToDatabase(encodedNewPass, principal);
      log.info("Save the new password in to the database");
    } catch (MessagingException e) {
      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
      response.setResponse("Failed to send new password!");
      e.printStackTrace();
    }
    return response;
  }

  private void verifyLogin(AccountLoginDTO accountLoginDTO) {
    Optional<Account> optionalAccount =
        accountService.findAccountByEmail(accountLoginDTO.getEmail());
    if (optionalAccount.isPresent()) {
      log.warn("Account is not found");
      Account account = optionalAccount.get();
      if (!account.isVerified()) {
        log.warn("Account email is not verified yet");
        throw new AccountNotVerifiedException("Account e-mail is not verified yet!");
      }
    }
  }
}
