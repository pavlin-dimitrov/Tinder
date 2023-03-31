package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.dto.EmailDetailsDto;
import com.volasoftware.tinder.service.contract.EmailService;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class EmailServiceImpl implements EmailService {

  private final JavaMailSender javaMailSender;
  private static final String verificationToken =
      "http://localhost:8080/api/v1/verify-email/verify";
  private static final String subject = "Verify Your Email";
  private static final String pass_subject = "NEW PASSWORD HERE";

  @Override
  public void sendVerificationEmail(String recipientEmail, String token) throws MessagingException {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());
      helper.setFrom(new InternetAddress("tinderapplicationsender@gmail.com"));
      helper.setTo(recipientEmail);
      helper.setSubject(setVerificationTokenEmailContent(recipientEmail, token).getSubject());
      helper.setText(setVerificationTokenEmailContent(recipientEmail, token).getMsgBody(), true);
      log.info("New email details generated");
      javaMailSender.send(message);
    } catch (Exception e) {
      throw new MessagingException("Failed on email sending!");
    }
  }

  @Override
  public void sendPasswordRecoveryEmail(String email, String newPassword)
      throws MessagingException {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name());
      helper.setFrom(new InternetAddress("tinderapplicationsender@gmail.com"));
      helper.setTo(email);
      helper.setSubject(setPasswordRecoveryEmailContent(email, newPassword).getSubject());
      helper.setText(setPasswordRecoveryEmailContent(email, newPassword).getMsgBody(), true);
      log.info("New email details generated");
      javaMailSender.send(message);
    } catch (Exception e) {
      throw new MessagingException("Failed on email sending!");
    }
  }

  private EmailDetailsDto setVerificationTokenEmailContent(String recipientEmail, String token) {
    EmailDetailsDto mail = new EmailDetailsDto();
    mail.setRecipient(recipientEmail);
    mail.setSubject(subject);
    mail.setMsgBody(
        String.format(
            "<html>"
                + "<body>"
                + "<p>Please verify your email by clicking the following button:</p>"
                + "<form action='"
                + verificationToken
                + "' method='POST'>"
                + "<button type='submit' name='token' value='"
                + token
                + "'>Verify Email</button>"
                + "</form>"
                + "</body>"
                + "</html>",
            token));
    return mail;
  }

  private EmailDetailsDto setPasswordRecoveryEmailContent(String recipient, String newPassword) {
    EmailDetailsDto mail = new EmailDetailsDto();
    mail.setRecipient(recipient);
    mail.setSubject(pass_subject);
    mail.setMsgBody(
        String.format(
            "<html>"
                + "<body>"
                + "<p>Please verify your new password by clicking the button:</p>"
                + "<p>This is your new password: "
                + newPassword
                + "</p>"
                + "</body>"
                + "</html>",
            newPassword));
    return mail;
  }
}
