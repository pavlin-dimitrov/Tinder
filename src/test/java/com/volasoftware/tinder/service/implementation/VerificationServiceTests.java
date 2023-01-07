package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@TestConfiguration
public class VerificationServiceTests {
    @Autowired
    private VerificationTokenService verificationTokenService;
    private VerificationTokenRepository verificationTokenRepository;

    @BeforeEach
    public void setUp() {
        verificationTokenRepository = mock(VerificationTokenRepository.class);
        verificationTokenService = mock(VerificationTokenService.class);
    }
    @Test
    public void testCreateVerificationToken(){
        Account account = new Account();
        account.setId(1L);
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setEmail("john.doe@gmail.com");

        VerificationToken token = new VerificationToken();
        token.setAccount(account);
        token.setToken("abcd-1234-abcd-1234");

        assertEquals(account, token.getAccount());
    }

    @Test
    public void testFindByToken(){
        Account account = new Account();
        account.setId(1L);
        account.setFirstName("John");
        account.setLastName("Doe");
        account.setEmail("john.doe@gmail.com");


        VerificationToken token = new VerificationToken();
        token.setAccount(account);
        token.setToken("abcd-1234-abcd-1234");

        when(verificationTokenRepository.findByToken("abcd-1234-abcd-1234")).thenReturn(token);
        VerificationToken result = verificationTokenRepository.findByToken("abcd-1234-abcd-1234");
        assertEquals(token, result);
    }

    @Test
    public void testDeleteExpiredTokens() {
        Account account1 = new Account();
        account1.setId(1L);
        account1.setFirstName("John");
        account1.setLastName("Doe");
        account1.setEmail("john.doe@gmail.com");

        Account account2 = new Account();
        account2.setId(2L);
        account2.setFirstName("Jane");
        account2.setLastName("Doe");
        account2.setEmail("jane.doe@gmail.com");

        VerificationToken expiredToken1 = new VerificationToken();
        expiredToken1.setAccount(account1);
        expiredToken1.setToken("abcd-1234-abcd-1234");
        expiredToken1.setExpirationDate(OffsetDateTime.now().minusDays(1));

        VerificationToken expiredToken2 = new VerificationToken();
        expiredToken2.setAccount(account2);
        expiredToken2.setToken("efgh-5678-efgh-5678");
        expiredToken2.setExpirationDate(OffsetDateTime.now().minusDays(1));

        VerificationToken validToken = new VerificationToken();
        validToken.setAccount(account2);
        validToken.setToken("ijkl-9101-ijkl-9101");
        validToken.setExpirationDate(OffsetDateTime.now().plusDays(1));

        List<VerificationToken> tokens = new ArrayList<>();
        tokens.add(expiredToken1);
        tokens.add(expiredToken2);
        tokens.add(validToken);

        when(verificationTokenRepository.findByExpirationDateBefore(any(OffsetDateTime.class))).thenReturn(tokens);
        verificationTokenService.deleteExpiredTokens();
        verify(verificationTokenService, times(1)).deleteExpiredTokens();

        verify(verificationTokenRepository, times(0)).delete(validToken);
        }
}