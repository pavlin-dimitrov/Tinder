package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.exeption.SendingVerificationEmailException;
import javax.mail.MessagingException;

public interface EmailService {
  void sendVerificationEmail(String accountEmail, String token)
      throws MessagingException, SendingVerificationEmailException;
}