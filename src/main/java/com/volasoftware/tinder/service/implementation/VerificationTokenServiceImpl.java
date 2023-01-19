package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.repository.VerificationTokenRepository;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
//@RequiredArgsConstructor
@NoArgsConstructor
@AllArgsConstructor
public class VerificationTokenServiceImpl implements VerificationTokenService {

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Override
  public VerificationToken createVerificationToken(Account account) {
    log.info("Creating verification token for user with id: {}", account.getId());
    VerificationToken token = new VerificationToken();
    token.setAccount(account);
    token.setToken(UUID.randomUUID().toString());
    OffsetDateTime expirationDate = OffsetDateTime.now().plusDays(2);
    token.setExpirationDate(expirationDate);
    return verificationTokenRepository.save(token);
  }

  @Override
  public VerificationToken findByToken(String tokenString) {
    log.info("Searching for verification token: {}", tokenString);
    return verificationTokenRepository.findByToken(tokenString);
  }

  @Override
  public void deleteExpiredTokens() {
    log.info("Deleting expired verification tokens");
    OffsetDateTime now = OffsetDateTime.now();
    List<VerificationToken> expiredTokens =
        verificationTokenRepository.findByExpirationDateBefore(now);
    verificationTokenRepository.deleteAll(expiredTokens);
  }

  @Override
  public void delete(VerificationToken token){
    log.info("Deleting expired verification token");
    verificationTokenRepository.delete(token);
  }
}