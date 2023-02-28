package com.volasoftware.tinder.controller;

import com.volasoftware.tinder.DTO.AccountDTO;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
//import java.net.http.HttpHeaders;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.http.HttpHeaders;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class AccountControllerTest {


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
  void testGetAllAccounts() {
    String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    long  accessTokenTwentyFourMinutes = 1000 * 60 * 24;
    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);

    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("USER"));

    String token = Jwts
        .builder()
        .claim("authorities", authorities.stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList()))
        .setSubject("pavlin.k.dimitrov@gmail.com")
        .setIssuedAt(new Date(System.currentTimeMillis()))
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenTwentyFourMinutes))
        .signWith(Keys.hmacShaKeyFor(keyBytes), SignatureAlgorithm.HS256)
        .compact();

    webTestClient.get()
        .uri("/api/v1/accounts")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
        .exchange()
        .expectStatus().isOk()
        .expectBodyList(AccountDTO.class)
        .hasSize(20);
  }

//  @Test
//  void testGetAllAccounts() {
//    List<AccountDTO> accounts = accountService.getAccounts();
//    assertNotNull(accounts);
//    assertEquals(20, accounts.size());
//    assertFalse(accounts.isEmpty());
//}

  @Test
  void getAllAccounts() throws Exception {}

  @Test
  void editAccountInfo() {
  }

  @Test
  void showUserProfile() {
  }
}