package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.dto.AccountLoginDto;
import com.volasoftware.tinder.dto.AccountRegisterDto;
import com.volasoftware.tinder.dto.AuthenticationResponseDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.AccountNotVerifiedException;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.exception.MissingRefreshTokenException;
import com.volasoftware.tinder.mapper.AccountRegisterMapper;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.JwtService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;

import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthenticationServiceImplTest {

  @Autowired private AuthenticationService underTest;
  @Mock private VerificationTokenService verificationTokenService;
  @Mock private AuthenticationManager authenticationManager;
  @Mock private AccountService accountService;
  @Mock private PasswordEncoder passwordEncoder;
  @Mock private JwtService jwtService;
  @Mock private EmailService emailService;
  private final static String fixedSalt =
      "$2a$10$9WzT8ofq96tEFe/LaIWxCeQl.XDfvew96SDECVoR7jKk9x.Oi1FJi";
  private final static String hashedPassword = BCrypt.hashpw("testPassword", fixedSalt);

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
  @DisplayName("Register new user with correct data!")
  void testRegisterNewAccountWhenCorrectDataIsGivenThenReturnSuccessResponse() {
    // given
    AccountRegisterDto registerDto = new AccountRegisterDto();
    registerDto.setEmail("john.doe@gmail.com");
    registerDto.setPassword("password");

    ResponseDto expected = new ResponseDto();
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

    String actual = authenticationService.register(registerDto).getResponse();
    assertEquals(expected.getResponse(), actual);
  }

  @Test
  @DisplayName("Try to Register new user with wrong Email address.")
  void testRegisterNewAccountWhenWrongEmailIsGivenThenExpectException() throws MessagingException {
    // given
    AccountRegisterDto registerDto = new AccountRegisterDto();
    registerDto.setEmail("john.doe@gmail.com");
    registerDto.setPassword("password");

    ResponseDto expected = new ResponseDto();
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
    String actual = authenticationService.register(registerDto).getResponse();
    assertEquals(expected.getResponse(), actual);
  }

  @Test
  @DisplayName("Try to Register new user when Email is taken")
  void testRegisterNewAccountWhenGivenEmailIsTakenThenExpectException() {
    // given
    AccountRegisterDto registerDto = new AccountRegisterDto();
    registerDto.setEmail("john.doe@gmail.com");
    registerDto.setPassword("password");
    Account account = AccountRegisterMapper.INSTANCE.mapAccountRegisterDtoToAccount(registerDto);
    when(accountService.findAccountByEmail(registerDto.getEmail()))
        .thenReturn(Optional.of(account));

    // when
    assertThatThrownBy(() -> underTest.register(registerDto))
        .isInstanceOf(EmailIsTakenException.class)
        .hasMessage("Email is taken! Use another e-mail address!");
  }

  @Test
  @DisplayName("Login when correct data is given")
  void testToLoginWhenCorrectDataIsGivenThenExpectAuthResponseDto() {
    // given
    Account account = new Account();
    account.setId(1L);
    account.setEmail("john.doe@gmail.com");
    account.setPassword(hashedPassword);
    account.setVerified(true);

    AccountLoginDto accountLoginDto = new AccountLoginDto();
    accountLoginDto.setPassword("testPassword");
    accountLoginDto.setEmail("john.doe@gmail.com");

    Authentication authentication =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDto.getEmail(), accountLoginDto.getPassword()));

    when(accountService.getAccountByEmailIfExists(accountLoginDto.getEmail())).thenReturn(account);
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                accountLoginDto.getEmail(), accountLoginDto.getPassword())))
        .thenReturn(authentication);
    when(jwtService.generateAccessToken(account)).thenReturn("Access Token");
    when(jwtService.generateRefreshToken(account)).thenReturn("Refresh Token");

    // when
    AuthenticationResponseDto response = underTest.login(accountLoginDto);
    // then
    assertEquals(response.getAccessToken(), "Access Token");
    assertEquals(response.getRefreshToken(), "Refresh Token");
  }

  @Test
  @DisplayName("Login when account is not Verified")
  void testToLoginWhenNotVerifiedAccountIsGivenThenExpectException() {
    // given
    Account account = new Account();
    account.setId(1L);
    account.setEmail("john.doe@gmail.com");
    account.setPassword(hashedPassword);
    account.setVerified(false);

    AccountLoginDto accountLoginDto = new AccountLoginDto();
    accountLoginDto.setPassword("testPassword");
    accountLoginDto.setEmail("john.doe@gmail.com");

    when(accountService.getAccountByEmailIfExists(accountLoginDto.getEmail())).thenReturn(account);

    // when and then
    assertThatThrownBy(() -> underTest.login(accountLoginDto))
        .isInstanceOf(AccountNotVerifiedException.class)
        .hasMessage("Account e-mail is not verified yet!");
  }

  @Test
  @DisplayName("Login with not existing email")
  void testToLoginWhitNotExistingEmailThenExpectException() {
    // given
    String nonExistingEmail = "nonexistingemail@example.com";
    AccountLoginDto accountLoginDto = new AccountLoginDto();
    accountLoginDto.setEmail(nonExistingEmail);
    accountLoginDto.setPassword("password");

    when(accountService.getAccountByEmailIfExists(accountLoginDto.getEmail()))
        .thenThrow(new AccountNotFoundException());

    // when and then
    assertThatThrownBy(() -> underTest.login(accountLoginDto))
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessage("User not found");
  }

  @Test
  @DisplayName("Get new Pair of Authentication Tokens")
  void testGetNewPairAuthTokensWhenRefreshTokenExistsThenExpectNewPair() throws IOException {
    // given
  }

  @Test
  @DisplayName("Try to Get new pair of Authentication tokens when refresh token is missing")
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
  @DisplayName("Recover password when correct data is given")
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
  @DisplayName("Try to Recover password, then expect exception")
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
    ResponseDto response = underTest.recoverPassword(principal);
    //then
    assertThat(response.getResponse()).isEqualTo("Failed to send new password!");
  }
}
