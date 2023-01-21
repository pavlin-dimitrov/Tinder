package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.exception.EmailIsVerifiedException;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.EmailVerificationService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;

import java.time.OffsetDateTime;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.security.auth.login.AccountNotFoundException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@NoArgsConstructor
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private AccountService accountService;

    @Override
    public ResponseEntity<?> verifyEmail(String token) throws AccountNotFoundException {
        VerificationToken verificationToken = verificationTokenService.findByToken(token);

        if (verificationToken == null
                || verificationToken.getExpirationDate().isBefore(OffsetDateTime.now())) {
            log.warn("Token expired or invalid: {}", token);
            return ResponseEntity.badRequest().body("Token expired or invalid");
        } else {
            AccountVerificationDTO accountVerificationDTO =
                    accountService.findAccountById(verificationToken.getAccount().getId()).orElse(null);

            if (accountVerificationDTO == null) {
                log.warn("User not found for token: {}", token);
                return ResponseEntity.badRequest().body("User not found");
            }

            Long accountId = verificationToken.getAccount().getId();
            accountVerificationDTO.setVerified(true);
            accountService.updateVerificationStatus(accountId, accountVerificationDTO);

            log.info("Successfully verified email for user with id: {}", accountId);
            return ResponseEntity.ok().build();
        }
    }

    @Override
    public void resendVerificationEmail(String email) throws AccountNotFoundException {
        Account account = isAccountPresent(email);
        isEmailVerified(email);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(account);
        log.info("Re-Verification token generated for email: {}", account.getEmail());
        verificationTokenService.updateToken(verificationToken);
        log.info("Re-Verification token saved in to dthe DB");
        try {
            emailService.sendVerificationEmail(account.getEmail(), verificationToken.getToken());
            log.info("Email with re-verification token sent to email: {}", account.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
            e.printStackTrace();
        }
    }

    private Account isAccountPresent(String email) throws AccountNotFoundException {
        Optional<Account> optionalAccount = accountService.findAccountByEmail(email);
        if (optionalAccount.isEmpty()) {
            throw new AccountNotFoundException("Account with e-mail: " + email + " is not found!");
        } else {
            return optionalAccount.get();
        }
    }

    private void isEmailVerified(String email) throws AccountNotFoundException {
        if (isAccountPresent(email).isVerified()) {
            throw new EmailIsVerifiedException("This e-mail: " + email + " is already verified!");
        }
    }
}
