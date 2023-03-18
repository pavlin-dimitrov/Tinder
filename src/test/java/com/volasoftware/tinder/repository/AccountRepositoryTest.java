package com.volasoftware.tinder.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.volasoftware.tinder.dto.AccountRegisterDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.mapper.AccountRegisterMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class AccountRepositoryTest {

  @Autowired
  AccountRepository underTest;

  @AfterEach
  void tearDown() {
    underTest.deleteAll();
  }

  @Test
  void testFindAccountByEmailWhenCorrectEmailIsPassedThenReturnTrue() {
    //given
    String email = "john.doe@gmail.com";
    AccountRegisterDto accountRegister = new AccountRegisterDto();
    accountRegister.setFirstName("John");
    accountRegister.setLastName("Doe");
    accountRegister.setEmail(email);
    accountRegister.setAge(29);
    accountRegister.setGender(Gender.MALE);
    underTest.save(AccountRegisterMapper.INSTANCE.mapAccountRegisterDtoToAccount(accountRegister));
    //when
    Optional<Account> account = underTest.findAccountByEmail(email);
    boolean expected = account.isPresent();
    //then
    assertThat(expected).isTrue();

  }

  @Test
  void testFindAccountByEmailWhenNotExistingEmailIsPassedThenReturnFalse(){
    //given
    String email = "john.doe@gmail.com";
    //when
    Optional<Account> account = underTest.findAccountByEmail(email);
    boolean expected = account.isPresent();
    //then
    assertThat(expected).isFalse();
  }

  @Test
  void testFindAllByTypeWhenGivenDataIncludesTwoAccountsTypeRealThenFindTwoAccounts() {
    //given
    accounts();
    //when
    List<Account> resultAccounts = underTest.findAllByType(AccountType.REAL);
    //then
    assertEquals(2, resultAccounts.size());
    assertNotNull(resultAccounts);
  }

  @Test
  void testFindAllWhenAllAccountsAreEqualToThreeThenReturnEquals() {
    //given
    accounts();
    //when
    int numberOfAccounts = underTest.findAll().size();
    //then
    assertEquals(3, numberOfAccounts);;
    assertNotNull(underTest.findAll());
  }

  private void accounts() {
    Account account1 = new Account();
    account1.setType(AccountType.REAL);
    underTest.save(account1);
    Account account2 = new Account();
    account2.setType(AccountType.REAL);
    underTest.save(account2);
    Account account3 = new Account();
    account3.setType(AccountType.BOT);
    underTest.save(account3);
  }
}