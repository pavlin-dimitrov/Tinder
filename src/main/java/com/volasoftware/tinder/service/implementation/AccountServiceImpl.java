package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.dto.AccountVerificationDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.enums.AccountType;
import com.volasoftware.tinder.enums.Role;
import com.volasoftware.tinder.exception.AccountNotFoundException;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import com.volasoftware.tinder.mapper.AccountMapper;
import com.volasoftware.tinder.mapper.AccountVerificationMapper;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;
import java.security.Principal;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AccountServiceImpl implements AccountService {
  private static final String DEFAULT_IMAGE_LINK =
      "https://drive.google.com/file/d/1W1viYGAN02JMMPbBnbewuaCdR9OHQS1r/view?usp=share_link";
  private final AccountRepository accountRepository;

  @Override
  public Account saveAccount(Account account) {
    if (account.getImage() == null) {
      account.setImage(DEFAULT_IMAGE_LINK);
    }
    if (account.getRole() == null) {
      account.setRole(Role.USER);
    }
    if (account.getType() == null) {
      account.setType(AccountType.REAL);
    }
    return accountRepository.save(account);
  }

  @Override
  public Optional<Account> findAccountByEmail(String email) {
    log.info("Get account by e-mail with e-mail: " + email);
    return accountRepository.findAccountByEmail(email);
  }

  @Override
  public AccountDto findAccountById(Long id) {
    Account account = getAccountByIdIfExists(id);
    return AccountMapper.INSTANCE.mapAccountToAccountDto(account);
  }

  @Override
  public Page<AccountDto> getAccounts(Pageable pageable) {
    log.info("Get all accounts");
    Page<Account> accountsPage = accountRepository.findAll(pageable);
    return accountsPage.map(AccountMapper.INSTANCE::mapAccountToAccountDto);
  }

  @Override
  public AccountVerificationDto findAccountVerificationById(Long id) {
    log.info("Get isVerified field for Account with ID: " + id);
    Account account = getAccountByIdIfExists(id);
    return AccountVerificationMapper.INSTANCE.accountToAccountVerificationDto(account);
  }

  @Override
  @Transactional
  public void updateVerificationStatus(Long accountId, AccountVerificationDto verificationDto) {
    Account account = getAccountByIdIfExists(accountId);
    log.info(String.format("Update verification status for e-mail: %s", account.getEmail()));
    AccountVerificationMapper.INSTANCE.updateAccountFromVerificationDto(verificationDto, account);
    accountRepository.save(account);
  }

  @Override
  @Transactional
  public AccountDto updateAccountInfo(AccountDto accountDto, Principal principal)
      throws NotAuthorizedException {
    validateAccountOwnership(accountDto, principal);

    Account account = getAccountByIdIfExists(accountDto.getId());

    account.setFirstName(accountDto.getFirstName());
    account.setLastName(accountDto.getLastName());
    account.setEmail(accountDto.getEmail());
    account.setGender(accountDto.getGender());

    accountRepository.save(account);
    log.info("Account changes are saved!");
    return AccountMapper.INSTANCE.mapAccountToAccountDto(account);
  }

  @Override
  public void saveNewPasswordInToDatabase(String newPassword, Account account) {
    log.info(String.format("Update password for account with e-mail: %s", account.getEmail()));
    account.setPassword(newPassword);
    accountRepository.save(account);
  }

  @Override
  public Account getAccountByIdIfExists(Long id) {
    return accountRepository.findById(id).orElseThrow(AccountNotFoundException::new);
  }

  @Override
  public Account getAccountByEmailIfExists(String email) {
    return accountRepository.findAccountByEmail(email).orElseThrow(AccountNotFoundException::new);
  }

  private void validateAccountOwnership(AccountDto accountDto, Principal principal)
      throws NotAuthorizedException {
    if (!principal.getName().equals(accountDto.getEmail())) {
      log.warn(
          "Account: "
              + principal.getName()
              + " is not authorized to edit account of: "
              + accountDto.getEmail());
      throw new NotAuthorizedException();
    }
  }
}
