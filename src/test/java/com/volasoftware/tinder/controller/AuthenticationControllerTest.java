package com.volasoftware.tinder.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.AuthenticationResponseDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.mapper.AccountLoginMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

  @Autowired AccountRepository repository;
  @Autowired AccountService service;
  @Autowired PasswordEncoder passwordEncoder;
  @LocalServerPort private int port;
  private WebTestClient webTestClient;
  private static final Logger log = LogManager.getLogger(AuthenticationControllerTest.class);

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer().baseUrl("http://localhost:" + port).build();
  }

  @Test
  @DisplayName("Register new user with status 200")
  void testRegisterNewUserWhenCorrectDataIsGivenThenReturnStatusOk() {
    AccountRegisterDTO registerDTO = new AccountRegisterDTO();
    registerDTO.setEmail("test@example.com");
    registerDTO.setPassword("Aa012345678");
    registerDTO.setFirstName("Test");
    registerDTO.setLastName("User");
    registerDTO.setGender(Gender.MALE);
    registerDTO.setAge(34);

    webTestClient
        .post()
        .uri("/api/v1/auth/register")
        .body(Mono.just(registerDTO), AccountRegisterDTO.class)
        .exchange()
        .expectStatus()
        .isOk()
        .expectBody(ResponseDTO.class)
        .value(
            response -> {
              assertThat(response.getResponse())
                  .isEqualTo("Check your e-mail to confirm the registration");
            });
  }

  @Test
  @DisplayName("Try register new user with provided not valid password, then return status 400")
  void testRegisterWithNotValidPassword() {
    AccountRegisterDTO registerDTO = new AccountRegisterDTO();
    registerDTO.setEmail("test@example.com");
    registerDTO.setPassword("012345");
    registerDTO.setFirstName("Test");
    registerDTO.setLastName("User");
    registerDTO.setGender(Gender.MALE);
    registerDTO.setAge(34);

    webTestClient
        .post()
        .uri("/api/v1/auth/register")
        .body(Mono.just(registerDTO), AccountRegisterDTO.class)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  void login() {
    Account account = Account.builder()
        .firstName("John")
        .lastName("Doe")
        .email("pavlin.k.dimitrov@gmail.com")
        .role(Role.USER)
        .gender(Gender.MALE)
        .age(30)
        .type(AccountType.REAL)
        .isVerified(true)
        .password("Aa012345678")
        .build();
    service.saveAccount(account);

    Account account1 = service.getAccountByEmailIfExists("pavlin.k.dimitrov@gmail.com");
    AccountLoginDTO loginDTO = AccountLoginMapper.INSTANCE.mapAccountToAccountLoginDTO(account1);

    this.webTestClient
        .post()
        .uri("/api/v1/auth/login")
        .body(Mono.just(loginDTO), AccountLoginDTO.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AuthenticationResponseDTO.class)
        .value(response -> {assertThat(response.getAccessToken()).isNotEmpty();
        assertThat(response.getRefreshToken()).isNotEmpty();
        });
  }

  @Test
  void getNewPairAuthTokens() {}

  @Test
  void recoverPassword() {}
}
