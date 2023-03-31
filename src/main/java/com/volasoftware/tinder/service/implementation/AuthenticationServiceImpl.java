package com.volasoftware.tinder.service.implementation;

import static org.springframework.http.HttpStatus.FORBIDDEN;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.dto.AccountLoginDto;
import com.volasoftware.tinder.dto.AccountRegisterDto;
import com.volasoftware.tinder.dto.AuthenticationResponseDto;
import com.volasoftware.tinder.dto.ResponseDto;
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
  public ResponseDto register(AccountRegisterDto accountRegisterDto) {
    log.info("Register new account with email {}", accountRegisterDto.getEmail());
    checkForExistingEmail(accountRegisterDto);
    ResponseDto response = new ResponseDto();
    Account account = getAccount(accountRegisterDto);

    VerificationToken token = verificationTokenService.createVerificationToken(account);
    log.info("Verification token generated for email: {}", accountRegisterDto.getEmail());
    try {
      emailService.sendVerificationEmail(accountRegisterDto.getEmail(), token.getToken());
      log.info("Email with verification token sent to email: {}", accountRegisterDto.getEmail());
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
  public AuthenticationResponseDto login(AccountLoginDto accountLoginDto) {
    Account user = accountService.
        getAccountByEmailIfExists(accountLoginDto.getEmail());

    checkIfPasswordMatches(accountLoginDto);
    log.info("Correct password is passed.");

    verifyLogin(accountLoginDto);
    log.info("Login verified.");

    getSecurityContext(accountLoginDto);
    log.info("Authentication successfully added to Security Context Holder");

    String accessToken = jwtService.generateAccessToken(user);
    log.info("Access token created after the Login.");

    String refreshToken = jwtService.generateRefreshToken(user);
    log.info("Refresh token created after Login.");

    return AuthenticationResponseDto.builder()
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  @Override
  public void getNewPairAuthTokens(HttpServletRequest request, HttpServletResponse response)
      throws IOException {
    final String authHeader = request.getHeader("Authorization");
    final String refresh_jwt;
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      try {
        refresh_jwt = authHeader.substring(7);
        setJwtResponseInHttpServlet(response, refresh_jwt);
      } catch (Exception e) {
        setErrorResponseInHttpServlet(response, e);
      }
    } else {
      log.warn("Refresh token is missing, can not refresh the access token.");
      throw new MissingRefreshTokenException();
    }
  }

  @Override
  public ResponseDto recoverPassword(Principal principal) {
    Account account = accountService.getAccountByEmailIfExists(principal.getName());
    String newPassword = UUID.randomUUID().toString();
    ResponseDto response = new ResponseDto();

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

  private Account getAccount(AccountRegisterDto accountRegisterDto) {
    Account account =
        AccountRegisterMapper.INSTANCE.mapAccountRegisterDtoToAccount(accountRegisterDto);
    account.setPassword(passwordEncoder.encode(accountRegisterDto.getPassword()));
    account = accountService.saveAccount(account);
    return account;
  }

  private void checkForExistingEmail(AccountRegisterDto accountRegisterDto) {
    Optional<Account> accountByEmail =
        accountService.findAccountByEmail(accountRegisterDto.getEmail());
    if (accountByEmail.isPresent()) {
      throw new EmailIsTakenException();
    }
  }

  private void verifyLogin(AccountLoginDto accountLoginDto) {
    Account account = accountService.getAccountByEmailIfExists(accountLoginDto.getEmail());
    if (!account.isVerified()) {
      log.warn("Account email is not verified yet");
      throw new AccountNotVerifiedException();
    }
  }

  private void getSecurityContext(AccountLoginDto accountLoginDto) {
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDto.getEmail(), accountLoginDto.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authentication);
    log.info("Security filter chain was SET");
  }

  private void setErrorResponseInHttpServlet(HttpServletResponse response, Exception e) throws IOException {
    log.error("Error refreshing the access token: {}", e.getMessage());
    response.setHeader("error", e.getMessage());
    response.setStatus(FORBIDDEN.value());
    Map<String, String> error = new HashMap<>();
    error.put("error_message", e.getMessage());
    new ObjectMapper().writeValue(response.getOutputStream(), error);
  }

  private void setJwtResponseInHttpServlet(HttpServletResponse response, String refreshToken)
      throws IOException {
    String userEmail = jwtService.extractUsername(refreshToken);
    Account account = accountService.getAccountByEmailIfExists(userEmail);
    String accessToken = jwtService.generateAccessToken(account);
    Map<String, String> tokens = new HashMap<>();
    tokens.put("refreshToken", refreshToken);
    tokens.put("accessToken", accessToken);
    response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
    new ObjectMapper().writeValue(response.getOutputStream(), tokens);
  }

  private void checkIfPasswordMatches(AccountLoginDto accountLoginDto) {
    Account account = accountService.getAccountByEmailIfExists(accountLoginDto.getEmail());
    String enteredPassword = accountLoginDto.getPassword();
    String storedHash = account.getPassword();
    if (!BCrypt.checkpw(enteredPassword, storedHash)){
      throw new InvalidPasswordException();
    }
  }
}
