package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.entity.EmailDetails;
import com.volasoftware.tinder.service.contract.EmailService;
import java.nio.charset.StandardCharsets;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailServiceImpl implements EmailService {

  @Autowired private JavaMailSender javaMailSender;
  private final static String verificationToken = "http://localhost:8080/api/v1/accounts/verify?token=%s";
  private final static String subject = "Verify Your Email";

  @Override
  public void sendVerificationEmail(String recipientEmail, String token) throws MessagingException {
    try {
      MimeMessage message = javaMailSender.createMimeMessage();
      MimeMessageHelper helper =
          new MimeMessageHelper(
              message,
              MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
              StandardCharsets.UTF_8.name()); // true
      helper.setFrom(new InternetAddress("tinderapplicationsender@gmail.com"));
      helper.setTo(recipientEmail);
      helper.setSubject(mail(recipientEmail, token).getSubject());
      helper.setText(mail(recipientEmail, token).getMsgBody(), true);
      log.info("New email details generated");
      javaMailSender.send(message);
    } catch (Exception e) {
      throw new MessagingException("Failed on email sending!");
    }
  }

  private EmailDetails mail(String recipientEmail, String token) {
    EmailDetails mail = new EmailDetails();
    mail.setRecipient(recipientEmail);
    mail.setSubject(subject);
    mail.setMsgBody(
        String.format(
            "<html>"
                + "<body>"
                + "<p>Please verify your email by clicking the following button:</p>"
                + "<form action='"+ verificationToken +"' method='GET'>"
                + "<button type='submit' name='token' value='" + token +"'>Verify Email</button>"
                + "</form>"
                + "</body>"
                + "</html>",
            token));
    return mail;
  }
}