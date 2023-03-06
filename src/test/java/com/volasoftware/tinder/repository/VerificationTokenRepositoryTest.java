package com.volasoftware.tinder.repository;

import static org.junit.jupiter.api.Assertions.*;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class VerificationTokenRepositoryTest {

  @Autowired VerificationTokenRepository underTest;
  @Autowired AccountRepository accountRepository;

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
  }

  @Test
  void
      testIfCanFindTokenByGivenAccountAndTokenThenAccountIdAndAccountIdFromTheTokenShouldBeEquals() {
    // given
    Account account = createAccount("john.doe@gmail.com");
    accountRepository.save(account);
    VerificationToken token = createVerificationToken(account, OffsetDateTime.now().plusDays(2));
    underTest.save(token);
    // when
    underTest.findByToken(token.getToken());
    // then
    assertEquals(account.getId(), token.getAccount().getId());
  }

  @Test
  void testFindByExpirationDateBeforeWhenListIsGivenThenReturnTwoExpired() {
    // given
    Account account1 = createAccount("john.doe@gmail.com");
    Account account2 = createAccount("j.doe@gmail.com");
    Account account3 = createAccount("jo.doe@gmail.com");
    accountRepository.saveAll(Arrays.asList(account1, account2, account3));
    VerificationToken token1 = createVerificationToken(account1, OffsetDateTime.now().plusDays(2));
    VerificationToken token2 = createVerificationToken(account2, OffsetDateTime.now().minusDays(2));
    VerificationToken token3 = createVerificationToken(account3, OffsetDateTime.now().minusDays(2));
    underTest.saveAll(Arrays.asList(token1, token2, token3));
    //when
    List<VerificationToken> expired = underTest.findByExpirationDateBefore(OffsetDateTime.now());
    //then
    assertEquals(2, expired.size());
  }

  @Test
  void testToDeleteAllByExpirationDateBeforeNowThenExpectedOneToken() {
    // given
    Account account1 = createAccount("john.doe@gmail.com");
    Account account2 = createAccount("j.doe@gmail.com");
    Account account3 = createAccount("jo.doe@gmail.com");
    accountRepository.saveAll(Arrays.asList(account1, account2, account3));
    VerificationToken token1 = createVerificationToken(account1, OffsetDateTime.now().plusDays(2));
    VerificationToken token2 = createVerificationToken(account2, OffsetDateTime.now().minusDays(2));
    VerificationToken token3 = createVerificationToken(account3, OffsetDateTime.now().minusDays(2));
    underTest.saveAll(Arrays.asList(token1, token2, token3));
    //when
    underTest.deleteAllByExpirationDateBefore(OffsetDateTime.now());
    //then
    assertEquals(1, underTest.findAll().size());
  }

  private Account createAccount(String email) {
    Account account = new Account();
    account.setEmail(email);
    return account;
  }

  private VerificationToken createVerificationToken(
      Account account, OffsetDateTime expirationDate) {
    VerificationToken token = new VerificationToken();
    token.setToken(UUID.randomUUID().toString());
    token.setAccount(account);
    token.setExpirationDate(expirationDate);
    return token;
  }
}
