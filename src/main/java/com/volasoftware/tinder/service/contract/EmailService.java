package com.volasoftware.tinder.service.contract;

import javax.mail.MessagingException;

public interface EmailService {
  void sendVerificationEmail(String accountEmail, String token) throws MessagingException;
}