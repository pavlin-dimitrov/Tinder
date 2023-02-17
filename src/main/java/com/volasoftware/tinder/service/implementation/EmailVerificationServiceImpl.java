package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.exception.EmailIsVerifiedException;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.EmailVerificationService;
import com.volasoftware.tinder.service.contract.FriendsService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.time.OffsetDateTime;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailVerificationServiceImpl implements EmailVerificationService {

    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final AccountService accountService;
    private final FriendsService friendsService;

    @Override
    public ResponseEntity<?> verifyEmail(String token) throws AccountNotFoundException {
        VerificationToken verificationToken = verificationTokenService.findByToken(token);

        if (verificationToken == null
                || verificationToken.getExpirationDate().isBefore(OffsetDateTime.now())) {
            log.warn("Token expired or invalid: {}", token);
            return ResponseEntity.badRequest().body("Token expired or invalid");
        } else {
            AccountVerificationDTO accountVerificationDTO =
                    accountService.findAccountVerificationById(verificationToken.getAccount().getId());

            if (accountVerificationDTO == null) {
                log.warn("User not found for token: {}", token);
                return ResponseEntity.badRequest().body("User not found");
            }

            Long accountId = verificationToken.getAccount().getId();
            accountVerificationDTO.setVerified(true);
            accountService.updateVerificationStatus(accountId, accountVerificationDTO);

            friendsService.linkFriendsAsync(accountId);

            log.info("Successfully verified email for user with id: {}", accountId);
            return ResponseEntity.ok().build();
        }
    }

    @Override
    public ResponseDTO resendVerificationEmail(String email) throws AccountNotFoundException {
        Account account = isAccountPresent(email);
        isEmailVerified(email);
        VerificationToken verificationToken = verificationTokenService.createVerificationToken(account);
        log.info("Re-Verification token generated for email: {}", account.getEmail());
        verificationTokenService.updateToken(verificationToken);
        log.info("Re-Verification token saved in to the DB");
        ResponseDTO response = new ResponseDTO();
        try {
            emailService.sendVerificationEmail(account.getEmail(), verificationToken.getToken());
            response.setResponse("Check your e-mail to confirm the registration");
            log.info("Email with re-verification token sent to email: {}", account.getEmail());
        } catch (MessagingException e) {
            log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
            response.setResponse("Failed to re-send verification e-mail!");
            e.printStackTrace();
        }
        return response;
    }

    private Account isAccountPresent(String email) throws AccountNotFoundException {
        Optional<Account> optionalAccount = accountService.findAccountByEmail(email);
        if (optionalAccount.isEmpty()) {
            log.warn("Account was not found!");
            throw new AccountNotFoundException("Account with e-mail: " + email + " is not found!");
        } else {
            return optionalAccount.get();
        }
    }

    private void isEmailVerified(String email) throws AccountNotFoundException {
        if (isAccountPresent(email).isVerified()) {
            log.warn("Email address for email: " + email + " is already verified");
            throw new EmailIsVerifiedException("This e-mail: " + email + " is already verified!");
        }
    }
}
