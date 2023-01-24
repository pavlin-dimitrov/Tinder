package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;

import java.security.Principal;
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
  private final AccountRepository accountRepository;
  private final ModelMapper modelMapper;

  @Override
  public Account saveAccount(Account account) {
    return accountRepository.save(account);
  }

  @Override
  public Optional<Account> findAccountByEmail(String email) {
    log.info("Get account by e-mail with e-mail: " + email);
    return accountRepository.findAccountByEmail(email);
  }

  @Override
  public List<AccountDTO> getAccounts() {
    log.info("Get all accounts");
    return accountRepository.findAll().stream()
        .map(account -> modelMapper.map(account, AccountDTO.class))
        .collect(Collectors.toList());
  }

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
    if (account == null) {
      log.warn("Account with ID: {} was not found!", accountId);
      throw new AccountNotFoundException("Account with ID: " + accountId + " was not found");
    }
    log.info(String.format("Update verification status for e-mail: %s", account.getEmail()));
    modelMapper.map(verificationDTO, account);
    accountRepository.save(account);
  }

  @Override
  @Transactional
  public AccountDTO updateAccountInfo(AccountDTO accountDTO, Principal principal)
      throws NotAuthorizedException {
    if (!principal.getName().equals(accountDTO.getEmail())) {
      log.warn("Account: " + principal.getName() + " is not authorized to edit account of: "
              + accountDTO.getEmail());
      throw new NotAuthorizedException("Not authorized to edit this account!");
    }

    Account account = accountRepository.findById(accountDTO.getId())
            .orElseThrow(() -> new AccountNotFoundException(
                        "Account with id: " + accountDTO.getId() + " was not found!"));

    account.setFirstName(accountDTO.getFirstName());
    account.setLastName(accountDTO.getLastName());
    account.setEmail(accountDTO.getEmail());
    account.setGender(accountDTO.getGender());

    accountRepository.save(account);
    log.info("Account changes are saved!");

    return modelMapper.map(account, AccountDTO.class);
  }
}
