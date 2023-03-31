package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.dto.AccountDto;
import com.volasoftware.tinder.dto.AccountVerificationDto;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import java.security.Principal;
import java.util.Optional;
import javax.security.auth.login.AccountNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  Account saveAccount(Account account);

  Optional<Account> findAccountByEmail(String email);

  AccountDto findAccountById(Long id);

  Page<AccountDto> getAccounts(Pageable pageable);

  AccountVerificationDto findAccountVerificationById(Long id);

  void updateVerificationStatus(Long accountId, AccountVerificationDto verificationDto)
      throws AccountNotFoundException;

  AccountDto updateAccountInfo(AccountDto accountDto, Principal principal)
      throws NotAuthorizedException;

  void saveNewPasswordInToDatabase(String newPassword, Account account);

  Account getAccountByIdIfExists(Long id);

  Account getAccountByEmailIfExists(String email);
}
