package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import com.volasoftware.tinder.repository.AccountRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
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
  public void testWhenRetrieveAllAccountsThenExpectedListOfThreeAccountsToBeReturned() {
    List<Account> accounts = getAccounts();
    when(repository.findAll()).thenReturn(accounts);
    List<AccountDTO> result = service.getAccounts();
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(repository).findAll();
  }

  @Test
  @DisplayName("Test creating account")
  public void testCreateAccountThenExpectSuccessfulAccountCreation() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    when(repository.save(captor.capture())).thenReturn(account);
    Account createdAccount = service.saveAccount(account);
    assertThat(createdAccount).isEqualTo(account);
  }

  @Test
  @DisplayName("Find account by valid e-mail address")
  public void testFindAccountByEmailWhenTheGivenAddressIsValidThenFindAccount() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    when(repository.findAccountByEmail("john.doe@example.com")).thenReturn(Optional.of(account));
    Optional<Account> returnedAccount = service.findAccountByEmail("john.doe@example.com");
    assertThat(returnedAccount).isEqualTo(Optional.of(account));
  }

  @Test
  @DisplayName("Find account by e-mail address, using invalid address")
  public void testFindAccountByEmailWhenTheGivenAddressIsNotValidThenFindThatAccountIsEmpty() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    when(repository.findAccountByEmail("invalid@example.com")).thenReturn(Optional.empty());
    Optional<Account> returnedAccount = service.findAccountByEmail("invalid@example.com");
    assertThat(returnedAccount).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Find account verification by valid Id")
  public void testFindAccountVerificationWhenValidIdIsProvidedAccountVerificationDTO() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    account.setVerified(true);
    AccountVerificationDTO accountVerificationDTO =
        new AccountVerificationDTO(account.isVerified());

    Mockito.when(repository.findById(account.getId())).thenReturn(Optional.of(account));
    Mockito.when(modelMapper.map(account, AccountVerificationDTO.class))
        .thenReturn(accountVerificationDTO);

    Optional<AccountVerificationDTO> returnedAccount =
        service.findAccountVerificationById(account.getId());
    verify(repository).findById(account.getId());
    assertTrue(returnedAccount.isPresent());
    assertTrue(returnedAccount.get().isVerified());
  }

  @Test
  @DisplayName("Update verification status for valid account")
  public void testWhenUpdatingVerificationStatusForAccountThenExpectedTrue() {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    account.setVerified(false);
    AccountVerificationDTO verificationDTO = new AccountVerificationDTO(true);

    Mockito.when(repository.findById(account.getId())).thenReturn(Optional.of(account));

    Mockito.doAnswer(
            invocation -> {
              Object[] args = invocation.getArguments();
              AccountVerificationDTO dto = (AccountVerificationDTO) args[0];
              Account a = (Account) args[1];
              a.setVerified(dto.isVerified());
              return null;
            })
        .when(modelMapper)
        .map(Mockito.any(AccountVerificationDTO.class), Mockito.any(Account.class));

    service.updateVerificationStatus(account.getId(), verificationDTO);

    verify(repository).findById(account.getId());
    verify(repository).save(account);
    assertTrue(account.isVerified());
  }

  @Test
  @DisplayName("Update account info for authorized user")
  public void testUpdateAccountInfoWhenUserIsAuthorizedThenExpectedAccountCorrectlyUpdated()
      throws NotAuthorizedException {
    Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);

    AccountDTO accountDTO = new AccountDTO();
    Mockito.when(modelMapper.map(account, AccountDTO.class)).thenReturn(accountDTO);
    accountDTO = modelMapper.map(account, AccountDTO.class);
    accountDTO.setFirstName("Jane");
    accountDTO.setLastName("Doe");
    accountDTO.setEmail("jane.doe@example.com");
    accountDTO.setGender(Gender.FEMALE);

    AccountDTO finalAccountDTO = accountDTO;
    Principal principal = finalAccountDTO::getEmail;
    Mockito.when(repository.findById(accountDTO.getId())).thenReturn(Optional.of(account));
    AccountDTO updatedAccount = service.updateAccountInfo(accountDTO, principal);

    verify(repository).findById(accountDTO.getId());
    verify(repository).save(account);

    assertEquals("Jane", updatedAccount.getFirstName());
    assertEquals("Doe", updatedAccount.getLastName());
    assertEquals("jane.doe@example.com", updatedAccount.getEmail());
    assertEquals(Gender.FEMALE, updatedAccount.getGender());
  }

  @Test
  @DisplayName(
      "Throw NotAuthorizedException when unauthorized user attempts to update account info")
  public void testUpdateAccountInfoWhenUserIsNotAuthorizedThenThrowAnNotAuthorizedException() {
    AccountDTO accountDTO = new AccountDTO(
        1L,
        "John",
        "Doe",
        "john.doe@example.com",
        Gender.MALE
    );

    Account account = getAccount(
        "Johny",
        "Doe",
        "john.doe@example.com",
        Gender.MALE
    );

    account.setId(1L);
    Principal principal = () -> "jane.doe@example.com";
    Mockito.when(repository.findById(accountDTO.getId())).thenReturn(Optional.of(account));

    assertThrows(
        NotAuthorizedException.class, () -> service.updateAccountInfo(accountDTO, principal));
    verify(repository, never()).save(account);
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
