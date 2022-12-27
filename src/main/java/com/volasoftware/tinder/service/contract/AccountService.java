package com.volasoftware.tinder.service.contract;

import com.volasoftware.tinder.entity.Account;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface AccountService {

  List<Account> getAccounts();

  Account addNewAccount(Account account);

}
