package com.volasoftware.tinder.service.implementation;

import static org.aspectj.bridge.MessageUtil.fail;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.Gender;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.EmailIsTakenException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class AccountServiceTests {
    @Mock private AccountRepository repository;
    @Mock private ModelMapper modelMapper;
    @InjectMocks private AccountServiceImpl service;

    @Test
    public void testGetAllAccounts() {
        List<Account> accounts = getAccounts();
        when(repository.findAll()).thenReturn(accounts);
        List<AccountDTO> result = service.getAccounts();
        assertNotNull(result);
        assertEquals(3, result.size());
        verify((repository).findAll());
    }

    @Test
    public void testCreateAccount() {
        Account account = getAccount("John", "Doe", "john.doe@example.com", Gender.MALE);
        ArgumentCaptor<Account> captor = ArgumentCaptor.forClass(Account.class);
        when(repository.save(captor.capture())).thenReturn(account);
        Account createdAccount = service.saveAccount(account);
        assertThat(createdAccount).isEqualTo(account);
    }

    @Test
    @DisplayName("Test creating account with taken email")
    public void testCreateAccountWithTakenEmail() {
        Account account = getAccounts().get(0);
        Account newAccount = new Account();
        newAccount.setEmail(account.getEmail());

        when(repository.findAccountByEmail(account.getEmail())).thenReturn(Optional.of(newAccount));

        try {
            service.saveAccount(account);
            fail("Expected EmailTakenException to be thrown");
        } catch (Exception e) {
            verify(repository).findAccountByEmail(account.getEmail());
        }
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
