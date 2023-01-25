package com.volasoftware.tinder.service.implementation;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.repository.AccountRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AccountServiceTests {
  @Mock private AccountRepository repository;
  @Mock private ModelMapper modelMapper;
  @InjectMocks private AccountServiceImpl service;
  @Mock private AccountVerificationDTO accountVerificationDTO;

  @Test
  @DisplayName("Test get all accounts")
  public void testGetAllAccounts() {
    List<Account> accounts = getAccounts();
    when(repository.findAll()).thenReturn(accounts);
    List<AccountDTO> result = service.getAccounts();
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(repository).findAll();
  }

  @Test
  @DisplayName("Test creating account")
  public void testCreateAccount() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    when(repository.save(captor.capture())).thenReturn(account);
    Account createdAccount = service.saveAccount(account);
    assertThat(createdAccount).isEqualTo(account);
  }

//  @Test
//  @DisplayName("Test creating account with taken email")
//  public void testCreateAccountWithTakenEmail() {
//    Account account = getAccounts().get(0);
//    Account newAccount = new Account();
//    newAccount.setEmail(account.getEmail());
//    when(repository.findAccountByEmail(account.getEmail())).thenReturn(Optional.of(newAccount));
//
//    assertThrows(EmailIsTakenException.class, () -> service.saveAccount(account));
//
//    verify(repository).findAccountByEmail(account.getEmail());
//  }

  @Test
  @DisplayName("Find account by valid e-mail address")
  public void testFindAccountByEmail_validEmail() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    when(repository.findAccountByEmail("john.doe@example.com")).thenReturn(Optional.of(account));
    Optional<Account> returnedAccount = service.findAccountByEmail("john.doe@example.com");
    assertThat(returnedAccount).isEqualTo(Optional.of(account));
  }

  @Test
  @DisplayName("Find account by e-mail address, using invalid address")
  public void testFindAccountByEmail_invalidEmail() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    when(repository.findAccountByEmail("invalid@example.com")).thenReturn(Optional.empty());
    Optional<Account> returnedAccount = service.findAccountByEmail("invalid@example.com");
    assertThat(returnedAccount).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Find account by valid Id")
  public void testFindAccountVerificationById_validId() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    account.setVerified(true);

    when(repository.findById(account.getId())).thenReturn(Optional.of(account));

    Optional<AccountVerificationDTO> returnedAccount = service.findAccountVerificationById(account.getId());
    verify(repository).findById(account.getId());
    assertTrue(returnedAccount.isPresent());
    assertTrue(returnedAccount.get().isVerified());
  }

  private Account getAccount(String firstName, String lastName, String email, Gender gender) {
    Account account = new Account();
    account.setFirstName(firstName);
    account.setLastName(lastName);
    account.setEmail(email);
    account.setPassword("password");
    account.setGender(gender);
    account.setRole(Role.USER);
    return account;
  }

  private List<Account> getAccounts() {
    Account account1 = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    Account account2 = getAccount("Jane", "Smith", "jane.smith@example.com", Gender.FEMALE);
    Account account3 = getAccount("Bob", "Johnson", "bob.johnson@example.com", Gender.MALE);
    return Arrays.asList(account1, account2, account3);
  }
}
