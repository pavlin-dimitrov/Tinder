package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.DTO.AccountVerificationDTO;
import com.volasoftware.tinder.entity.Account;
import java.util.List;
import java.util.Optional;

import javax.security.auth.login.AccountNotFoundException;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  Account save(Account account);

  Optional<Account> findAccountByEmail(String email);

  List<AccountDTO> getAccounts();

  Optional<AccountVerificationDTO> findAccountById(Long id);

  void updateVerificationStatus(Long accountId, AccountVerificationDTO verificationDTO)
      throws AccountNotFoundException;

  Account updateAccountInfo(AccountDTO accountDTO);
}
