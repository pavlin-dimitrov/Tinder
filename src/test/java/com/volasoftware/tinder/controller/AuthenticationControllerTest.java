package com.volasoftware.tinder.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.volasoftware.tinder.DTO.AccountLoginDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.AuthenticationResponseDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.mapper.AccountLoginMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AuthenticationControllerTest {

  @Autowired AccountRepository repository;

  @LocalServerPort private int port;

  private WebTestClient webTestClient;

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
    getAccounts();
    Account account = repository.findById(2L).get();
    AccountLoginDTO accountLoginDTO = AccountLoginMapper.INSTANCE.mapAccountToAccountLoginDTO(account);

    webTestClient
        .post()
        .uri("/api/v1/auth/login")
        .contentType(MediaType.APPLICATION_JSON)
        .body(Mono.just(accountLoginDTO), AccountLoginDTO.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AuthenticationResponseDTO.class)
        .value(response -> {
          assertThat(response.getAccessToken()).isNotEmpty();
          assertThat(response.getRefreshToken()).isNotEmpty();
        });
  }

  @Test
  void getNewPairAuthTokens() {}

  @Test
  void recoverPassword() {}

  private void getAccounts() {
    Account account = new Account();
    account.setId(21L);
    account.setEmail("pavlin.k.dimitrov@gmail.com");
    account.setPassword("Aa012345678");
    account.setAge(34);
    account.setVerified(true);
    account.setRole(Role.USER);
    account.setGender(Gender.MALE);
    repository.save(account);

    Account account1 = new Account();
    account1.setId(22L);
    account1.setEmail("test@example.com");
    account1.setPassword("Bb012345678");
    account1.setRole(Role.USER);
    account1.setGender(Gender.MALE);
    repository.save(account1);
  }

  private String getJwt(String email) {
    String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    long accessTokenTwentyFourMinutes = 1000 * 60 * 24;
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("USER"));

    return Jwts.builder()
        .claim(
            "authorities",
            authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenTwentyFourMinutes))
        .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
        .compact();
  }
}
