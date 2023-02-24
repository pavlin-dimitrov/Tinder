package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

@ExtendWith(MockitoExtension.class)
class VerificationTokenServiceImplTest {

  @Autowired private VerificationTokenService underTest;
  @Mock private VerificationTokenRepository repository;

  @BeforeEach
  public void setUp() {
    underTest = new VerificationTokenServiceImpl(repository);
  }

  @Test
  @DisplayName("Create verification token")
  public void testCreateVerificationToken() {
    // given
    Account account = getAccounts().get(0);
    when(repository.save(any(VerificationToken.class)))
        .thenAnswer(invocation -> invocation.getArgument(0));
    // when
    VerificationToken token = underTest.createVerificationToken(account);
    // then
    verify(repository).save(any(VerificationToken.class));
    assertThat(repository).isNotNull();
    assertThat(token.getAccount()).isEqualTo(account);
    assertThat(token.getToken()).isNotEmpty();
    assertThat(token.getExpirationDate())
        .isAfter(OffsetDateTime.now())
        .isBefore(OffsetDateTime.now().plusDays(2));
  }

  @Test
  @DisplayName("Find token by token string")
  public void testFindByToken() {
    // given
    String stringToken = getTokens().get(0).getToken();
    VerificationToken token = getTokens().get(0);
    when(repository.findByToken(stringToken)).thenReturn(token);
    // when
    VerificationToken result = underTest.findByToken(stringToken);
    assertEquals(token, result);
  }

  @Test
  @DisplayName("Delete expired tokens")
  public void testDeleteExpiredTokens() {
    // given
    List<VerificationToken> tokens = getTokens();

    when(repository.findByExpirationDateBefore(any(OffsetDateTime.class)))
        .thenReturn(List.of(tokens.get(1), tokens.get(2)));
    doNothing().when(repository).deleteAll(anyList());
    // when
    underTest.deleteExpiredTokens();
    // then
    verify(repository).findByExpirationDateBefore(any(OffsetDateTime.class));
    verify(repository).deleteAll(List.of(tokens.get(1), tokens.get(2)));
    verifyNoMoreInteractions(repository);
  }

  @Test
  @DisplayName("Update token string and expiration date")
  void testUpdateTokenWhenStringAndDateAreGivenThenExpectNewValidToken(){
    //given
    VerificationToken oldToken = getTokens().get(1);
    VerificationToken newToken = oldToken;
    newToken.setToken("47e3603b-dc99-4f47-9c9c-1231aae4f380");
    newToken.setExpirationDate(OffsetDateTime.now().plusDays(2));
    repository.save(newToken);
//    when(repository.findById(oldToken.getId())).thenReturn(Optional.of(oldToken));
    when(repository.save(oldToken)).thenReturn(newToken);
    //when
    underTest.updateToken(oldToken);
    //then
    assertThat(newToken.getExpirationDate()).isAfterOrEqualTo(OffsetDateTime.now());
    assertThat(newToken.getId()).isEqualTo(oldToken.getId());
    assertThat(newToken.getToken()).isNotEqualTo(oldToken.getToken());
  }

  private List<Account> getAccounts() {
    List<Account> accounts = new ArrayList<>();
    Account account1 =
        Account.builder().id(1L).firstName("Vratsa").email("john.doe@gmail.com").build();
    Account account2 =
        Account.builder().id(2L).firstName("Mezdra").email("jane.care@mail.com").build();
    Account account3 =
        Account.builder().id(3L).firstName("Sofia").email("bob.davide@gmail.com").build();

    accounts.add(account1);
    accounts.add(account2);
    accounts.add(account3);
    return accounts;
  }

  private List<VerificationToken> getTokens() {
    List<Account> accounts = getAccounts();
    List<VerificationToken> tokens = new ArrayList<>();
    VerificationToken token1 = new VerificationToken();
    token1.setId(1L);
    token1.setAccount(accounts.get(0));
    token1.setExpirationDate(OffsetDateTime.now().plusDays(2));

    VerificationToken token2 = new VerificationToken();
    token2.setId(2L);
    token2.setAccount(accounts.get(1));
    token2.setExpirationDate(OffsetDateTime.now().minusDays(1));

    VerificationToken token3 = new VerificationToken();
    token3.setId(3L);
    token3.setAccount(accounts.get(2));
    token3.setExpirationDate(OffsetDateTime.now().minusDays(2));

    tokens.add(token1);
    tokens.add(token2);
    tokens.add(token3);
    return tokens;
  }
}
