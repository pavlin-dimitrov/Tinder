package com.volasoftware.tinder.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {
  @Mock private AccountRepository repository;
  @InjectMocks private AccountServiceImpl underTest;
  private static final String DEFAULT_IMAGE_LINK =
      "https://drive.google.com/file/d/1W1viYGAN02JMMPbBnbewuaCdR9OHQS1r/view?usp=share_link";

  @BeforeEach
  void setUp() {
    underTest = new AccountServiceImpl(repository);
  }

  @Test
  @DisplayName("Test get all accounts")
  void testWhenRetrieveAllAccountsThenExpectedListOfThreeAccountsToBeReturned() {
    // given
    List<Account> accounts = getAccounts();
    when(repository.findAll()).thenReturn(accounts);
    // when
    List<AccountDTO> result = underTest.getAccounts();
    // then
    assertNotNull(result);
    assertEquals(3, result.size());
    verify(repository).findAll();
  }

  @Test
  @DisplayName("Test creating account")
  void testCreateAccountThenExpectSuccessfulAccountCreation() {
    // given
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
    when(repository.save(captor.capture())).thenReturn(account);
    // when
    Account createdAccount = underTest.saveAccount(account);
    // then
    assertThat(createdAccount).isEqualTo(account);
    assertThat(createdAccount.getType()).isEqualTo(AccountType.REAL);
    assertThat(createdAccount.getRole()).isEqualTo(Role.USER);
    assertThat(createdAccount.getImage()).isEqualTo(DEFAULT_IMAGE_LINK);
  }

  @Test
  @DisplayName("Find account by valid e-mail address")
  void testFindAccountByEmailWhenTheGivenAddressIsValidThenFindAccount() {
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    when(repository.findAccountByEmail("john.doe@example.com")).thenReturn(Optional.of(account));
    Optional<Account> returnedAccount = underTest.findAccountByEmail("john.doe@example.com");
    assertThat(returnedAccount).isEqualTo(Optional.of(account));
  }

  @Test
  @DisplayName("Find account by e-mail address, using invalid address")
  void testFindAccountByEmailWhenTheGivenAddressIsNotValidThenFindThatAccountIsEmpty() {
    // given
    when(repository.findAccountByEmail("invalid@example.com")).thenReturn(Optional.empty());
    // when
    Optional<Account> returnedAccount = underTest.findAccountByEmail("invalid@example.com");
    // then
    assertThat(returnedAccount).isEqualTo(Optional.empty());
  }

  @Test
  @DisplayName("Find account by given id")
  void testFindAccountByIdWhenTheCorrectIdIsGivenThenExpectCorrectAccountDTO() {
    // given
    Long accountId = 1L;
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(accountId);
    when(repository.findById(accountId)).thenReturn(Optional.of(account));
    // when
    AccountDTO accountDTO = underTest.findAccountById(accountId);
    // then
    assertNotNull(accountDTO);
    assertEquals(account.getId(), accountDTO.getId());
    verify(repository).findById(accountId);
  }

  @Test
  @DisplayName("Throw exception Account Not Found when wrong ID is given")
  void testFindAccountByIdWhenTheInvalidIdIsGivenThenExpectException() {
    // given
    Long accountId = 1L;
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(accountId);
    // when and then
    assertThatThrownBy(() -> underTest.findAccountById(2L))
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(repository, never()).save(any());
  }

  @Test
  @DisplayName("Find account verification by valid Id")
  void testFindAccountVerificationWhenValidIdIsProvidedAccountVerificationDTO() {
    // given
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    account.setVerified(true);
    Mockito.when(repository.findById(account.getId())).thenReturn(Optional.of(account));
    // when
    AccountVerificationDTO returnedAccount = underTest.findAccountVerificationById(account.getId());
    // then
    verify(repository).findById(account.getId());
    assertTrue(returnedAccount.isVerified());
  }

  @Test
  @DisplayName("Throw exception on Find Account Verification when invalid account Id is given")
  void testFindAccountVerificationWhenInvalidIdIsGivenThenExpectException() {
    // given
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    //when and then
    assertThatThrownBy(() -> underTest.findAccountVerificationById(2L))
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessageContaining("User not found");
  }

  @Test
  @DisplayName("Update verification status for valid account")
  void testWhenUpdatingVerificationStatusForAccountThenExpectedTrue() {
    // given
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    account.setVerified(false);
    AccountVerificationDTO verificationDTO = new AccountVerificationDTO(true);

    Mockito.when(repository.findById(account.getId())).thenReturn(Optional.of(account));
    // when
    underTest.updateVerificationStatus(account.getId(), verificationDTO);
    // then
    verify(repository).findById(account.getId());
    verify(repository).save(account);
    assertTrue(account.isVerified());
  }

  @Test
  @DisplayName("Update account info for authorized user")
  void testUpdateAccountInfoWhenUserIsAuthorizedThenExpectedAccountCorrectlyUpdated()
      throws NotAuthorizedException {
    // given
    Account account = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    AccountDTO accountDTO = AccountMapper.INSTANCE.mapAccountToAccountDto(account);
    accountDTO.setEmail("jane.doe@example.com");
    // when
    Principal principal = accountDTO::getEmail;
    Mockito.when(repository.findById(accountDTO.getId())).thenReturn(Optional.of(account));
    AccountDTO updatedAccount = underTest.updateAccountInfo(accountDTO, principal);
    // then
    verify(repository).findById(accountDTO.getId());
    verify(repository).save(account);
    assertEquals("jane.doe@example.com", updatedAccount.getEmail());
  }

  @Test
  @DisplayName(
      "Throw NotAuthorizedException when unauthorized user attempts to update account info")
  void testUpdateAccountInfoWhenUserIsNotAuthorizedThenThrowAnNotAuthorizedException() {
    // given
    AccountDTO accountDTO = new AccountDTO(1L, "John", "Doe", "john.doe@example.com", Gender.MALE);
    Account account = createAccount("Johny", "Doe", "john.doe@example.com", Gender.MALE);
    account.setId(1L);
    // when
    Principal principal = () -> "jane.doe@example.com";
    // then
    assertThrows(
        NotAuthorizedException.class, () -> underTest.updateAccountInfo(accountDTO, principal));
    verify(repository, never()).save(account);
  }

  @Test
  @DisplayName("Test saving new account password")
  void testSaveNewPasswordInToDatabase(){
    //given
    Account account = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE);
    account.setPassword("Aa012345678");
    String newPassword = "Bb012345678";
    //when
    underTest.saveNewPasswordInToDatabase(newPassword, account);
    //then
    assertThat(account.getPassword()).isEqualTo(newPassword);
  }

  @Test
  @DisplayName("Get account by e-mail if exists")
  void testGetAccountWhenGivenEmailExistsThenExpectAccount(){
    //given
    String email = "john.doe@gmail.com";
    Account account = createAccount("John", "Doe", "john.doe@gmail.com", Gender.MALE);
    when(repository.findAccountByEmail(email)).thenReturn(Optional.of(account));
    //when
    Account resultAccount = underTest.getAccountByEmailIfExists(email);
    //then
    assertThat(resultAccount).isEqualTo(account);
    verify(repository).findAccountByEmail(email);
  }

  @Test
  @DisplayName("Get account by e-mail if exists")
  void testGetAccountWhenGivenEmailNotExistsThenExpectException(){
    //given
    String email = "jane.doe@gmail.com";
    //then
    assertThatThrownBy(() -> underTest.getAccountByEmailIfExists(email))
        .isInstanceOf(AccountNotFoundException.class)
        .hasMessage("User not found");
  }

  private Account createAccount(String firstName, String lastName, String email, Gender gender) {
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
    Account account1 = createAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
    Account account2 = createAccount("Jane", "Smith", "jane.smith@example.com", Gender.FEMALE);
    Account account3 = createAccount("Bob", "Johnson", "bob.johnson@example.com", Gender.MALE);
    return Arrays.asList(account1, account2, account3);
  }
}
