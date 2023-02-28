package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.AuthenticationResponseDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.AccountNotVerifiedException;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.exception.MissingRefreshTokenException;
import com.volasoftware.tinder.mapper.AccountLoginMapper;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.mapper.AccountRegisterMapper;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.JwtService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Principal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.MimeTypeUtils;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Autowired private AuthenticationService underTest;
  @Mock private VerificationTokenService verificationTokenService;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private AccountService accountService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private EmailService emailService;

  @BeforeEach
  void setUp() {
    underTest =
        new AuthenticationServiceImpl(
            verificationTokenService,
            authenticationManager,
            accountService,
            passwordEncoder,
            jwtService,
            emailService);
  }

  @Test
  void testRegisterNewAccountWhenCorrectDataIsGivenThenReturnSuccessResponse() {
    // given
    AccountRegisterDTO registerDTO = new AccountRegisterDTO();
    registerDTO.setEmail("john.doe@gmail.com");
    registerDTO.setPassword("password");

    ResponseDTO expected = new ResponseDTO();
    expected.setResponse("Check your e-mail to confirm the registration");

    VerificationTokenService verificationTokenService = mock(VerificationTokenService.class);

    AuthenticationServiceImpl authenticationService =
        spy(
            new AuthenticationServiceImpl(
                verificationTokenService,
                authenticationManager,
                accountService,
                passwordEncoder,
                jwtService,
                emailService));
    doReturn(new VerificationToken()).when(verificationTokenService).createVerificationToken(any());

    String actual = authenticationService.register(registerDTO).getResponse();
    assertEquals(expected.getResponse(), actual);
  }

  @Test
  void testRegisterNewAccountWhenWrongEmailIsGivenThenExpectException() throws MessagingException {
    // given
    AccountRegisterDTO registerDTO = new AccountRegisterDTO();
    registerDTO.setEmail("john.doe@gmail.com");
    registerDTO.setPassword("password");

    ResponseDTO expected = new ResponseDTO();
    expected.setResponse("Failed to send verification e-mail!");

    VerificationTokenService verificationTokenService = mock(VerificationTokenService.class);
    EmailService emailService = mock(EmailService.class);

    AuthenticationServiceImpl authenticationService =
        spy(
            new AuthenticationServiceImpl(
                verificationTokenService,
                authenticationManager,
                accountService,
                passwordEncoder,
                jwtService,
                emailService));
    doReturn(new VerificationToken()).when(verificationTokenService).createVerificationToken(any());
    doThrow(new MessagingException())
        .when(emailService)
        .sendVerificationEmail(anyString(), anyString());
    // when
    String actual = authenticationService.register(registerDTO).getResponse();
    assertEquals(expected.getResponse(), actual);
  }

  @Test
  void testRegisterNewAccountWhenGivenEmailIsTakenThenExpectException() {
    // given
    AccountRegisterDTO registerDTO = new AccountRegisterDTO();
    registerDTO.setEmail("john.doe@gmail.com");
    registerDTO.setPassword("password");
    Account account = AccountRegisterMapper.INSTANCE.mapAccountRegisterDtoToAccount(registerDTO);
    when(accountService.findAccountByEmail(registerDTO.getEmail()))
        .thenReturn(Optional.of(account));

    // when
    assertThatThrownBy(() -> underTest.register(registerDTO))
        .isInstanceOf(EmailIsTakenException.class)
        .hasMessage("Email is taken! Use another e-mail address!");
  }

  @Test
  void testToLoginWhenCorrectDataIsGivenThenExpectAuthResponseDto() {
    // given
    Account account = new Account();
    account.setId(1L);
    account.setEmail("john.doe@gmail.com");
    account.setPassword("testPassword");
    account.setVerified(true);

    AccountLoginDTO accountLoginDTO =
        AccountLoginMapper.INSTANCE.mapAccountToAccountLoginDTO(account);
    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDTO.getEmail(), accountLoginDTO.getPassword()));

    when(accountService.getAccountByEmailIfExists(accountLoginDTO.getEmail())).thenReturn(account);
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDTO.getEmail(), accountLoginDTO.getPassword())))
        .thenReturn(authentication);
    when(jwtService.generateAccessToken(account)).thenReturn("Access Token");
    when(jwtService.generateRefreshToken(account)).thenReturn("Refresh Token");

    // when
    AuthenticationResponseDTO response = underTest.login(accountLoginDTO);
    // then
    assertEquals(response.getAccessToken(), "Access Token");
    assertEquals(response.getRefreshToken(), "Refresh Token");
  }

  @Test
  void testToLoginWhenNotVerifiedAccountIsGivenThenExpectException() {
    // given
    Account account = new Account();
    account.setId(1L);
    account.setEmail("john.doe@gmail.com");
    account.setPassword("testPassword");
    account.setVerified(false);

    AccountLoginDTO accountLoginDTO =
        AccountLoginMapper.INSTANCE.mapAccountToAccountLoginDTO(account);

    when(accountService.getAccountByEmailIfExists(accountLoginDTO.getEmail())).thenReturn(account);

    // when and then
    assertThatThrownBy(() -> underTest.login(accountLoginDTO))
        .isInstanceOf(AccountNotVerifiedException.class)
        .hasMessage("Account e-mail is not verified yet!");
  }

  @Test
  void testToLoginWhitNotExistingEmailThenExpectException() {
    // given
    String nonExistingEmail = "nonexistingemail@example.com";
    AccountLoginDTO accountLoginDTO = new AccountLoginDTO();
    accountLoginDTO.setEmail(nonExistingEmail);
    accountLoginDTO.setPassword("password");

    when(accountService.getAccountByEmailIfExists(accountLoginDTO.getEmail()))
        .thenThrow(new AccountNotFoundException());

    // when and then
    assertThatThrownBy(() -> underTest.login(accountLoginDTO))
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessage("User not found");
  }

  @Test
  void testGetNewPairAuthTokensWhenRefreshTokenExistsThenExpectNewPair() throws IOException {
    // given
  }

  @Test
  void testGetNewPairAuthTokensWhenRefreshTokenIsMissingThenExpectException() {
    // given
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    when(request.getHeader("Authorization")).thenReturn(null);

    // when and then
    assertThatThrownBy(() -> underTest.getNewPairAuthTokens(request, response))
        .isInstanceOf(MissingRefreshTokenException.class);
  }

  @Test
  void testRecoverPasswordWhenCorrectDataIsGivenThenExpectOldPassNotMatchingNewPass() {
    // given
    String oldPassword = "oldPassword";
    Account account = new Account();
    account.setId(1L);
    account.setEmail("john.doe@gmail.com");
    account.setPassword(oldPassword);

    Principal principal = Mockito.mock(Principal.class);
    when(principal.getName()).thenReturn(account.getEmail());

    when(accountService.getAccountByEmailIfExists(principal.getName())).thenReturn(account);
    // when
    underTest.recoverPassword(principal);
    // then
    assertFalse(passwordEncoder.matches(oldPassword, account.getPassword()));
  }

  @Test
  void testRecoverPasswordThenExpectMessagingException() throws MessagingException {
    // given
    ArgumentCaptor<String> newPasswordCaptor = ArgumentCaptor.forClass(String.class);
    String email = "test@example.com";
    String oldPass = "oldPassword";

    Account account = new Account();
    account.setEmail(email);
    account.setPassword(oldPass);

    Principal principal = mock(Principal.class);
    when(principal.getName()).thenReturn(email);

    when(accountService.getAccountByEmailIfExists(email)).thenReturn(account);

    doThrow(new MessagingException("Failed to send new password!"))
        .when(emailService)
        .sendPasswordRecoveryEmail(eq(email), newPasswordCaptor.capture());
    //when
    ResponseDTO response = underTest.recoverPassword(principal);
    //then
    assertThat(response.getResponse()).isEqualTo("Failed to send new password!");
  }
}
