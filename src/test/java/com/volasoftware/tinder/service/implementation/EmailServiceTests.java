//package com.volasoftware.tinder.service.implementation;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.fail;
//
//import com.icegreen.greenmail.util.GreenMail;
//import com.icegreen.greenmail.util.ServerSetup;
//import com.volasoftware.tinder.service.contract.EmailService;
//import javax.mail.MessagingException;
//import javax.mail.internet.MimeMessage;
//import org.junit.Test;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//
//public class EmailServiceTests {
//
//  private GreenMail greenMail;
//
//  @Autowired EmailService emailService;
//
//  @BeforeEach
//  public void setUp() {
//    greenMail = new GreenMail(new ServerSetup(25, null, "smtp"));
//    greenMail.start();
//  }
//
//  @AfterEach
//  public void tearDown() {
//    greenMail.stop();
//  }
//
//  @Test
//  public void testSendVerificationEmail() {
//    String recipientEmail = "test@example.com";
//    String token = "abc123";
//    try {
//      emailService.sendVerificationEmail(recipientEmail, token);
//      Thread.sleep(1000);
//      MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
//      assertEquals(1, receivedMessages.length);
//      assertEquals(recipientEmail, receivedMessages[0].getHeader("To", null));
//
//    } catch (MessagingException e) {
//      fail("sendVerificationEmail threw an exception: " + e.getMessage());
//    } catch (InterruptedException e) {
//      fail("Interrupted while waiting for email to be delivered: " + e.getMessage());
//    }
//  }
//}
