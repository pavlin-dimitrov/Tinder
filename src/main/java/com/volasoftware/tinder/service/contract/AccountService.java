package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.DTO.ResponseDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.exception.NotAuthorizedException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import javax.security.auth.login.AccountNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  Account saveAccount(Account account);

  Optional<Account> findAccountByEmail(String email);

  AccountDTO findAccountById(Long id);

  List<AccountDTO> getAccounts();

  Optional<AccountVerificationDTO> findAccountVerificationById(Long id);

  void updateVerificationStatus(Long accountId, AccountVerificationDTO verificationDTO)
      throws AccountNotFoundException;

  AccountDTO updateAccountInfo(AccountDTO accountDTO, Principal principal)
      throws NotAuthorizedException;

  void saveNewPasswordInToDatabase(String newPassword, Account account);

  Account getAccountByIdIfExists(Long id);

  Account getAccountByEmailIfExists(String email);
}
