//package com.volasoftware.tinder.service.implementation;
//
//import com.volasoftware.tinder.DTO.AccountDTO;
//import com.volasoftware.tinder.DTO.AccountRegisterDTO;
//import com.volasoftware.tinder.enums.Gender;
//import com.volasoftware.tinder.repository.AccountRepository;
//import com.volasoftware.tinder.service.contract.AccountService;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.junit4.SpringRunner;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNotNull;
//
//@RunWith(SpringRunner.class)
//@SpringBootTest
//public class AccountServiceTests {
//
//    @Autowired
//    private AccountService service;
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Test
//    public void testGetAllAccounts() {
//        AccountRegisterDTO account1 = new AccountRegisterDTO();
//        account1.setFirstName("John");
//        account1.setLastName("Doe");
//        account1.setEmail("johnn.doe@example.com");
//        account1.setPassword("PassJohn1234");
//        account1.setGender(Gender.MALE);
//        service.addNewAccount(account1);
//
//        AccountRegisterDTO account2 = new AccountRegisterDTO();
//        account2.setFirstName("Jane");
//        account2.setLastName("Doe");
//        account2.setEmail("jane.doe@example.com");
//        account2.setPassword("PassJane1234");
//        account2.setGender(Gender.FEMALE);
//        service.addNewAccount(account2);
//
//        List<AccountDTO> accounts = service.getAccounts();
//
//        assertNotNull(accounts);
//        assertEquals(6, accounts.size()); //TODO this test may not work if we put new records in DB
//    }
//
//    @Test
//    public void testCreateAccount() {
//        AccountRegisterDTO dto = new AccountRegisterDTO();
//        dto.setFirstName("John");
//        dto.setLastName("Doe");
//        dto.setEmail("john22.doe@example.com");
//        dto.setPassword("PassJohn123");
//        dto.setGender(Gender.MALE);
//
//        AccountRegisterDTO createdAccount = service.addNewAccount(dto);
//
//        assertNotNull(dto);
//        assertEquals("John", createdAccount.getFirstName());
//        assertEquals("Doe", createdAccount.getLastName());
//        assertEquals("john22.doe@example.com", createdAccount.getEmail());
//        assertEquals("PassJohn123", createdAccount.getPassword());
//        assertEquals(Gender.MALE, createdAccount.getGender());
//    }
//
//    @Test
//    @DisplayName("Test creating account with taken email")
//    public void testCreateAccountWithTakenEmail() {
//        AccountRegisterDTO account = new AccountRegisterDTO();
//        account.setPassword("Password123");
//        account.setEmail("john.doe@example.com");
//        account.setFirstName("John");
//        account.setLastName("Doe");
//        account.setGender(Gender.MALE);
//
//        try {
//            AccountRegisterDTO accountTwo = service.addNewAccount(account);
//        } catch (IllegalStateException e) {
//            assertEquals("Email is taken! Use another e-mail address!", e.getMessage());
//        }
//    }
//}
