package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceImpl implements AccountService {

//  private final VerificationTokenService verificationTokenService;
  private final AccountRepository accountRepository;
//  private final PasswordEncoder passwordEncoder;
  private final ModelMapper modelMapper;
  //private final EmailService emailService;

  @Override
  // TODO Changed from AccountRegisterDTO to AccountDTO (check if any error)
  public Optional<Account> getAccountByEmail(AccountDTO accountDTO) {
    log.info("Get account by e-mail with e-mail: " + accountDTO.getEmail());
    return accountRepository.findAccountByEmail(accountDTO.getEmail());
  }

  @Override
  public List<AccountDTO> getAccounts() {
    log.info("Get all accounts");
    return accountRepository.findAll().stream()
        .map(account -> modelMapper.map(account, AccountDTO.class))
        .collect(Collectors.toList());
  }

//  @Override
//  public AccountRegisterDTO addNewAccount(AccountRegisterDTO accountRegisterDTO) {
//    log.info("Register new account with email {}", accountRegisterDTO.getEmail());
//
//    Optional<Account> accountByEmail =
//        accountRepository.findAccountByEmail(accountRegisterDTO.getEmail());
//    if (accountByEmail.isPresent()) {
//      throw new EmailIsTakenException("Email is taken! Use another e-mail address!");
//    }
//
//    Account account = modelMapper.map(accountRegisterDTO, Account.class);
//    account.setPassword(passwordEncoder.encode(accountRegisterDTO.getPassword()));
//    account = accountRepository.save(account);
//
//    VerificationToken token = verificationTokenService.createVerificationToken(account);
//    log.info("Verification token generated for email: {}", accountRegisterDTO.getEmail());
//
//    try {
//      emailService.sendVerificationEmail(accountRegisterDTO.getEmail(), token.getToken());
//      log.info("Email with verification token sent to email: {}", accountRegisterDTO.getEmail());
//    } catch (MessagingException e) {
//      log.error("Failed to send email for: " + account.getEmail() + "\n" + e);
//      e.printStackTrace();
//    }
//
//    return modelMapper.map(account, AccountRegisterDTO.class);
//  }

  @Override
  public Optional<AccountVerificationDTO> findAccountById(Long id) {
    log.info("Get isVerified field for Account with ID: " + id);
    return accountRepository
        .findById(id)
        .map(account -> modelMapper.map(account, AccountVerificationDTO.class));
  }

  @Override
  @Transactional
  public void updateVerificationStatus(Long accountId, AccountVerificationDTO verificationDTO) {
    Account account = accountRepository.findById(accountId).orElse(null);
    if (account == null){
      log.warn("Account with ID: {} was not found!", accountId);
      throw new AccountNotFoundException("Account with ID: " + accountId + " was not found");
    }
    log.info(String.format("Update verification status for e-mail: %s", account.getEmail()));
    modelMapper.map(verificationDTO, account);
    accountRepository.save(account);
  }
}