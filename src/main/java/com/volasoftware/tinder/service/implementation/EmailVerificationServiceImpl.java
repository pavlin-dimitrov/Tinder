package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.EmailVerificationService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.time.OffsetDateTime;
import javax.security.auth.login.AccountNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class EmailVerificationServiceImpl implements EmailVerificationService {

  private VerificationTokenService verificationTokenService;
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
}
