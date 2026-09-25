package com.transmoney.accountservice.service;

import com.transmoney.accountservice.Entity.Account;
import com.transmoney.accountservice.Entity.AccountStatus;
import com.transmoney.accountservice.dto.AccountResponse;
import com.transmoney.accountservice.dto.CreateAccountRequest;
import com.transmoney.accountservice.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor

public class AccountService {
    AccountRepository accountRepository;
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating an account for : {} " + request.getEmail());

        if(accountRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Account already exited against this email: " + request.getEmail());
        }

        Account account = new Account();
        account.setAccountHolderName(request.getAccountHolderName());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setAccountType(request.getAccountType());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(request.getInitialDeposit());


    }
}
