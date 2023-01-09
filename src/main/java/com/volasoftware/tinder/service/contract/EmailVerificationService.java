package com.volasoftware.tinder.service.contract;

import javax.security.auth.login.AccountNotFoundException;
import org.springframework.http.ResponseEntity;

public interface EmailVerificationService {

  ResponseEntity<?> verifyEmail(String token) throws AccountNotFoundException;

}
