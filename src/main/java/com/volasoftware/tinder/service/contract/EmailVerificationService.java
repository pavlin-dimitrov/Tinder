package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.ResponseDTO;
import javax.security.auth.login.AccountNotFoundException;
import org.springframework.http.ResponseEntity;

public interface EmailVerificationService {

  ResponseEntity<?> verifyEmail(String token) throws AccountNotFoundException;

  ResponseDTO resendVerificationEmail(String email) throws AccountNotFoundException;

}
