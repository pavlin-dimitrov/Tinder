package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;

public interface VerificationTokenService {

  VerificationToken createVerificationToken(Account account);

  VerificationToken findByToken(String tokenString);

  void deleteExpiredTokens();

  void delete(VerificationToken token);

  public void updateToken(VerificationToken token);
}
