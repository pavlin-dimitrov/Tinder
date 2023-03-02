package com.volasoftware.tinder.service.implementation;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.AuthenticationResponseDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.exception.AccountNotVerifiedException;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.exception.InvalidPasswordException;
import com.volasoftware.tinder.exception.MissingRefreshTokenException;
import com.volasoftware.tinder.mapper.AccountRegisterMapper;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.JwtService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.io.IOException;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
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
  private final JwtService jwtService;
  private final EmailService emailService;

  @Override
  public ResponseDTO register(AccountRegisterDTO accountRegisterDTO) {
    log.info("Register new account with email {}", accountRegisterDTO.getEmail());
    checkForExistingEmail(accountRegisterDTO);
    ResponseDTO response = new ResponseDTO();
    Account account = getAccount(accountRegisterDTO);

    VerificationToken token = verificationTokenService.createVerificationToken(account);
    log.info("Verification token generated for email: {}", accountRegisterDTO.getEmail());
    try {
      emailService.sendVerificationEmail(accountRegisterDTO.getEmail(), token.getToken());
      log.info("Email with verification token sent to email: {}", accountRegisterDTO.getEmail());
      response.setResponse("Check your e-mail to confirm the registration");
    } catch (MessagingException messagingException) {
      log.error(
          "Failed to send email for: "
              + (account != null ? account.getEmail() : "Missing account")
              + "\n");
      response.setResponse("Failed to send verification e-mail!");
    }
    return response;
  }

  @Override
  public AuthenticationResponseDTO login(AccountLoginDTO accountLoginDTO) {
    Account user = accountService.
        getAccountByEmailIfExists(accountLoginDTO.getEmail());

    checkIfPasswordMatches(accountLoginDTO);
    log.info("Correct password is passed.");

    verifyLogin(accountLoginDTO);
    log.info("Login verified.");

    getSecurityContext(accountLoginDTO);
    log.info("Authentication successfully added to Security Context Holder");

    String accessToken = jwtService.generateAccessToken(user);
    log.info("Access token created after the Login.");

    String refreshToken = jwtService.generateRefreshToken(user);
    log.info("Refresh token created after Login.");

    return AuthenticationResponseDTO.builder()
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
        Account account = accountService.getAccountByEmailIfExists(userEmail);
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
      throw new MissingRefreshTokenException();
    }
  }

  @Override
  public ResponseDTO recoverPassword(Principal principal) {
    Account account = accountService.getAccountByEmailIfExists(principal.getName());
    String newPassword = UUID.randomUUID().toString();
    ResponseDTO response = new ResponseDTO();

    try {
      emailService.sendPasswordRecoveryEmail(account.getEmail(), newPassword);
      response.setResponse("Your password was successfully changed!");
      log.info("Email with recovered password was sent to email: {}", account.getEmail());
      String encodedNewPass = passwordEncoder.encode(newPassword);
      log.info("Encode the password");
      accountService.saveNewPasswordInToDatabase(encodedNewPass, account);
      log.info("Save the new password in to the database");
    } catch (MessagingException e) {
      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
      response.setResponse("Failed to send new password!");
    }
    return response;
  }

  private Account getAccount(AccountRegisterDTO accountRegisterDTO) {
    Account account =
        AccountRegisterMapper.INSTANCE.mapAccountRegisterDtoToAccount(accountRegisterDTO);
    account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));
    account = accountService.saveAccount(account);
    return account;
  }

  private void checkForExistingEmail(AccountRegisterDTO accountRegisterDTO) {
    Optional<Account> accountByEmail =
        accountService.findAccountByEmail(accountRegisterDTO.getEmail());
    if (accountByEmail.isPresent()) {
      throw new EmailIsTakenException();
    }
  }

  private void verifyLogin(AccountLoginDTO accountLoginDTO) {
    Account account = accountService.getAccountByEmailIfExists(accountLoginDTO.getEmail());
    if (!account.isVerified()) {
      log.warn("Account email is not verified yet");
      throw new AccountNotVerifiedException();
    }
  }

  private void getSecurityContext(AccountLoginDTO accountLoginDTO) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDTO.getEmail(), accountLoginDTO.getPassword()));
    System.out.println("AUTH OBJECT:   " + authentication.getCredentials().toString());
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("Security filter chain was SET");
  }

  private void checkIfPasswordMatches(AccountLoginDTO accountLoginDTO) {
    Account account = accountService.getAccountByEmailIfExists(accountLoginDTO.getEmail());
    String enteredPassword = accountLoginDTO.getPassword();
    String storedHash = account.getPassword();
    if (!BCrypt.checkpw(enteredPassword, storedHash)){
      throw new InvalidPasswordException();
    }
  }
}
