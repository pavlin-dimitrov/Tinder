package com.volasoftware.tinder.controller;

import static org.assertj.core.api.Assertions.assertThat;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.mapper.AccountMapper;
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
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class AccountControllerTest {

  @Autowired
  AccountRepository repository;

  @LocalServerPort
  private int port;

  private WebTestClient webTestClient;

  @BeforeEach
  void setUp() {
    webTestClient = WebTestClient.bindToServer()
        .baseUrl("http://localhost:" + port)
        .build();
  }

  @Test
  @DisplayName("Get all accounts")
  void testGetAllAccountsThenExpectStatusOK() {
    getAccounts();
    Account account = repository.findById(21L).get();
    String token = getJwt(account.getEmail());

    webTestClient.get()
        .uri("/api/v1/accounts")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(AccountDto.class)
        .hasSize(22);
  }

  @Test
  @DisplayName("Edit My Account Info")
  void testEditAccountInfoWhenAuthorizedUserIsGivenThenExpectStatusOk() {
    getAccounts();
    Account account = repository.findById(1L).get();
    String token = getJwt(account.getEmail());

    AccountDto updatedAccountDto = AccountMapper.INSTANCE.mapAccountToAccountDto(account);
    updatedAccountDto.setFirstName("John");
    updatedAccountDto.setLastName("Doe");
    updatedAccountDto.setGender(Gender.MALE);

    webTestClient.put()
        .uri("/api/v1/accounts/profile")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .body(Mono.just(updatedAccountDto), AccountDto.class)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AccountDto.class)
        .value(response -> {
          assertThat(response.getId()).isEqualTo(account.getId());
          assertThat(response.getFirstName()).isEqualTo(updatedAccountDto.getFirstName());
          assertThat(response.getLastName()).isEqualTo(updatedAccountDto.getLastName());
          assertThat(response.getEmail()).isEqualTo(updatedAccountDto.getEmail());
        });
  }

  @Test
  @DisplayName("Show User Public Profile")
  void testShowUserProfileThenExpectStatusOkAndAccountDtoInBody() {
    getAccounts();
    Account account = repository.findById(1L).get();
    String token = getJwt(account.getEmail());

    Account newAccount = repository.findById(2L).get();

    AccountDto accountDto = AccountMapper.INSTANCE.mapAccountToAccountDto(newAccount);

    webTestClient.get()
        .uri("/api/v1/accounts/profile/?id=" + accountDto.getId())
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus().isOk()
        .expectBody(AccountDto.class)
        .value(response -> {
          assertThat(response.getId()).isEqualTo(accountDto.getId());
          assertThat(response.getEmail()).isEqualTo(accountDto.getEmail());
        });
  }

  private void getAccounts() {
    Account account = new Account();
    account.setId(21L);
    account.setEmail("pavlin.k.dimitrov@gmail.com");
    account.setPassword("Aa012345678");
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
    long  accessTokenTwentyFourMinutes = 1000 * 60 * 24;
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("USER"));

    return Jwts
        .builder()
        .claim("authorities", authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()))
        .setSubject(email)
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenTwentyFourMinutes))
        .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
        .compact();
  }
}