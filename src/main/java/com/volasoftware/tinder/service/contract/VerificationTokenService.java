package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import java.util.Optional;
import java.util.UUID;

public interface VerificationTokenService {

  VerificationToken createVerificationToken(Account account);

  VerificationToken findByToken(UUID tokenString);

  void deleteExpiredTokens();
}
