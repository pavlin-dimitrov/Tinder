package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
  @Autowired private final VerificationTokenService verificationTokenService;
  @Autowired private final AccountRepository accountRepository;
  @Autowired private final ModelMapper modelMapper;
  @Autowired private JavaMailSender mailSender;

  public List<AccountDTO> getAccounts() {
    log.info("Get all accounts");
    return accountRepository.findAll().stream()
        .map(account -> modelMapper.map(account, AccountDTO.class))
        .collect(Collectors.toList());
  }

  public AccountRegisterDTO addNewAccount(AccountRegisterDTO accountRegisterDTO) {
    log.info("Register new account with email {}", accountRegisterDTO.getEmail());

    Optional<Account> accountByEmail =
        accountRepository.findAccountByEmail(accountRegisterDTO.getEmail());
    if (accountByEmail.isPresent()) {
      throw new IllegalStateException("Email is taken! Use another e-mail address!");
    }

    Account account = modelMapper.map(accountRegisterDTO, Account.class);
    account = accountRepository.save(account);

    VerificationToken token = verificationTokenService.createVerificationToken(account);

    try {
      sendVerificationEmail(account.getEmail(), token.getToken());
    } catch (MessagingException e) {
      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
      e.printStackTrace();
    }

    return modelMapper.map(account, AccountRegisterDTO.class);
  }

  @Async
  public void sendVerificationEmail(String accountEmail, String token) throws MessagingException {
    MimeMessage message = mailSender.createMimeMessage();
    MimeMessageHelper helper = new MimeMessageHelper(message, true);
    helper.setTo(accountEmail);
    helper.setFrom("petar_petov86@abv.bg");
    helper.setSubject("Verify Your Email");
    helper.setText(
        String.format(
            "<html>"
                + "<body>"
                + "<p>Please verify your email by clicking the following button:</p>"
                + "<form action='http://localhost:8080/verify-email?token=%s' method='POST'>"
                + "<button type='submit'>Verify Email</button>"
                + "</form><"
                + "/body>"
                + "</html>",
            token),
        true);

    mailSender.send(message);
  }
}
