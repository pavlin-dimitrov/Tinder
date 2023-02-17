package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
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
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
  public AccountDTO findAccountById(Long id) {
    Account account = getAccountByIdIfExists(id);
    return AccountMapper.INSTANCE.mapAccountToAccountDto(account);
  }

  @Override
  public List<AccountDTO> getAccounts() {
    log.info("Get all accounts");
    List<Account> accounts = accountRepository.findAll();
    return AccountMapper.INSTANCE.mapAccountListToAccountDtoList(accounts);
  }

  @Override
  public AccountVerificationDTO findAccountVerificationById(Long id) {
    log.info("Get isVerified field for Account with ID: " + id);
    Account account = getAccountByIdIfExists(id);
    return AccountVerificationMapper.INSTANCE.accountToAccountVerificationDto(account);
  }

  @Override
  @Transactional
  public void updateVerificationStatus(Long accountId, AccountVerificationDTO verificationDTO) {
    Account account = getAccountByIdIfExists(accountId);
    log.info(String.format("Update verification status for e-mail: %s", account.getEmail()));
    AccountVerificationMapper.INSTANCE.updateAccountFromVerificationDTO(verificationDTO, account);
    accountRepository.save(account);
  }

  @Override
  @Transactional
  public AccountDTO updateAccountInfo(AccountDTO accountDTO, Principal principal)
      throws NotAuthorizedException {
    if (!principal.getName().equals(accountDTO.getEmail())) {
      log.warn(
          "Account: "
              + principal.getName()
              + " is not authorized to edit account of: "
              + accountDTO.getEmail());
      throw new NotAuthorizedException();
    }

    Account account = getAccountByIdIfExists(accountDTO.getId());

    account.setFirstName(accountDTO.getFirstName());
    account.setLastName(accountDTO.getLastName());
    account.setEmail(accountDTO.getEmail());
    account.setGender(accountDTO.getGender());

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
    return accountRepository
        .findById(id)
        .orElseThrow(AccountNotFoundException::new);
  }

  @Override
  public Account getAccountByEmailIfExists(String email) {
    return accountRepository
        .findAccountByEmail(email)
        .orElseThrow(AccountNotFoundException::new);
  }
}
