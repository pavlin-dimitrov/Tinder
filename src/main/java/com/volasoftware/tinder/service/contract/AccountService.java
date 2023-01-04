package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  Optional<Account> getAccountByEmail(AccountRegisterDTO accountRegisterDTO);

  List<AccountDTO> getAccounts();

  AccountRegisterDTO addNewAccount(AccountRegisterDTO accountRegisterDTO);

}
