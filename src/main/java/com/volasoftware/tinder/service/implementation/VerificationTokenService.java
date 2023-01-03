package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VerificationTokenService {

  @Autowired private VerificationTokenRepository verificationTokenRepository;

  public VerificationToken createVerificationToken(Account account) {
    VerificationToken token = new VerificationToken();
    token.setAccount(account);
    token.setToken(UUID.randomUUID().toString());
    OffsetDateTime expirationDate = OffsetDateTime.now().plusDays(2);
    token.setExpirationDate(expirationDate);
    return verificationTokenRepository.save(token);
  }

  public VerificationToken findByToken(UUID tokenString) {
    return verificationTokenRepository.findByToken(tokenString);
  }

  public void deleteExpiredTokens() {
    OffsetDateTime now = OffsetDateTime.now();
    List<VerificationToken> expiredTokens =
        verificationTokenRepository.findByExpirationDateBefore(now);
    verificationTokenRepository.deleteAll(expiredTokens);
  }
}