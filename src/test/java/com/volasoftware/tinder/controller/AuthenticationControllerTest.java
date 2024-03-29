package com.volasoftware.tinder.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.volasoftware.tinder.dto.AccountLoginDto;
import com.volasoftware.tinder.dto.AccountRegisterDto;
import com.volasoftware.tinder.dto.AuthenticationResponseDto;
import com.volasoftware.tinder.dto.ResponseDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.service.contract.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

  @Autowired AccountService service;
  @LocalServerPort private int port;
  private WebTestClient webTestClient;
  private final static String fixedSalt =
      "$2a$10$9WzT8ofq96tEFe/LaIWxCeQl.XDfvew96SDECVoR7jKk9x.Oi1FJi";
  private final static String hashedPassword = BCrypt.hashpw("testPassword", fixedSalt);

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  @DisplayName("Register new user with status 200")
  void testRegisterNewUserWhenCorrectDataIsGivenThenReturnStatusOk() {
    AccountRegisterDto registerDto = new AccountRegisterDto();
    registerDto.setEmail("tester@example.com");
    registerDto.setPassword("Aa012345678");
    registerDto.setFirstName("Test");
    registerDto.setLastName("User");
    registerDto.setGender(Gender.MALE);
    registerDto.setAge(34);

    webTestClient
        .post()
        .uri("/api/v1/auth/register")
        .body(Mono.just(registerDto), AccountRegisterDto.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ResponseDto.class)
        .value(
            response -> {
              assertThat(response.getResponse())
                  .isEqualTo("Check your e-mail to confirm the registration");
            });
  }

  @Test
  @DisplayName("Try register new user with provided not valid password, then return status 400")
  void testRegisterWithNotValidPassword() {
    AccountRegisterDto registerDto = new AccountRegisterDto();
    registerDto.setEmail("test@example.com");
    registerDto.setPassword("012345");
    registerDto.setFirstName("Test");
    registerDto.setLastName("User");
    registerDto.setGender(Gender.MALE);
    registerDto.setAge(34);

    webTestClient
        .post()
        .uri("/api/v1/auth/register")
        .body(Mono.just(registerDto), AccountRegisterDto.class)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void testLoginWhenCorrectCredentialsArePassedThenExpectTokensNotNull() {
    Account account = Account.builder()
        .firstName("John")
        .lastName("Doe")
        .email("john.doe@gmail.com")
        .role(Role.USER)
        .gender(Gender.MALE)
        .age(30)
        .type(AccountType.REAL)
        .isVerified(true)
        .password(hashedPassword)
        .build();
    service.saveAccount(account);

    AccountLoginDto accountLoginDto = new AccountLoginDto();
    accountLoginDto.setPassword("testPassword");
    accountLoginDto.setEmail("john.doe@gmail.com");

    this.webTestClient
        .post()
        .uri("/api/v1/auth/login")
        .body(Mono.just(accountLoginDto), AccountLoginDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AuthenticationResponseDto.class)
        .value(response -> {assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
        });
  }

  @Test
  @Disabled
  void getNewPairAuthTokens() {}

  @Test
  @Disabled
  void recoverPassword() {}
}
