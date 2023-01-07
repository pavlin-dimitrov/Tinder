package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.entity.VerificationToken;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import com.volasoftware.tinder.service.contract.EmailService;
import com.volasoftware.tinder.service.contract.VerificationTokenService;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {
  @Autowired private final VerificationTokenService verificationTokenService;
  @Autowired private final AccountRepository accountRepository;
  @Autowired private final ModelMapper modelMapper;
  @Autowired private EmailService emailService;

  @Override
  public Optional<Account> getAccountByEmail(AccountRegisterDTO accountRegisterDTO){
   return accountRepository.findAccountByEmail(accountRegisterDTO.getEmail());
  }
  @Override
  public List<AccountDTO> getAccounts() {
    log.info("Get all accounts");
    return accountRepository.findAll().stream()
        .map(account -> modelMapper.map(account, AccountDTO.class))
        .collect(Collectors.toList());
  }
  @Override
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
    log.info("Verification token generated for email: {}", accountRegisterDTO.getEmail());

    try {
      emailService.sendVerificationEmail(accountRegisterDTO.getEmail(), token.getToken());
      log.info("Email with verification token sent to email: {}", accountRegisterDTO.getEmail());
    } catch (MessagingException e) {
      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
      e.printStackTrace();
    }

    return modelMapper.map(account, AccountRegisterDTO.class);
  }
  @Override
  public Optional<AccountVerificationDTO> findById(Long id) {
    return accountRepository.findById(id)
        .map(account -> modelMapper
            .map(account, AccountVerificationDTO.class));
  }
  @Override
  @Transactional
  public void updateVerificationStatus(Long accountId, AccountVerificationDTO verificationDTO) {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null) {
      throw new RuntimeException("Account not found with id: " + accountId);
    }
    modelMapper.map(verificationDTO, account);
    accountRepository.save(account);
  }
}