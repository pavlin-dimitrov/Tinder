package com.volasoftware.tinder.service.implementation;

import com.volasoftware.tinder.DTO.AccountDTO;
import com.volasoftware.tinder.DTO.AccountRegisterDTO;
import com.volasoftware.tinder.entity.Account;
import com.volasoftware.tinder.repository.AccountRepository;
import com.volasoftware.tinder.service.contract.AccountService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    @Autowired
    private final AccountRepository accountRepository;
    @Autowired
    private final ModelMapper modelMapper;

    public List<AccountDTO> getAccounts() {
        return accountRepository
                .findAll()
                .stream()
                .map(account -> modelMapper.map(account, AccountDTO.class))
                .collect(Collectors.toList());
    }

    public AccountRegisterDTO addNewAccount(AccountRegisterDTO accountRegisterDTO) {
        Optional<Account> accountByEmail = accountRepository.findAccountByEmail(accountRegisterDTO.getEmail());
        if (accountByEmail.isPresent()) {
            throw new IllegalStateException("Email is taken! Use another e-mail address!");
        }
        Account account = modelMapper.map(accountRegisterDTO, Account.class);
        account = accountRepository.save(account);
        return modelMapper.map(account, AccountRegisterDTO.class);
    }
}
