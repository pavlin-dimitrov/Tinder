package com.volasoftware.tinder.service.implementation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.mapper.AccountRegisterMapper;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.AuthenticationService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.JwtService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.time.OffsetDateTime;
import javax.mail.MessagingException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
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
  void register() throws MessagingException {
    // given
    AccountRegisterDTO registerDTO = new AccountRegisterDTO();
    registerDTO.setFirstName("John");
    registerDTO.setLastName("Doe");
    registerDTO.setGender(Gender.MALE);
    registerDTO.setEmail("john.doe@gmail.com");
    registerDTO.setPassword("Aa012345678");
    registerDTO.setAge(34);


    when(verificationTokenService.createVerificationToken(any(Account.class))).thenReturn(any(VerificationToken.class));
    ResponseDTO result = underTest.register(registerDTO);

    verify(emailService, times(1)).sendVerificationEmail(registerDTO.getEmail(), any(VerificationToken.class).getToken());
    assertEquals("Check your e-mail to confirm the registration", result.getResponse());
    assertDoesNotThrow(() -> underTest.register(registerDTO));
  }

  @Test
  void login() {}

  @Test
  void getNewPairAuthTokens() {}

  @Test
  void recoverPassword() {}
}
