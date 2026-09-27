package com.transmoney.accountservice.service;

import com.transmoney.accountservice.Entity.Account;
import com.transmoney.accountservice.Entity.AccountStatus;
import com.transmoney.accountservice.Entity.AccountType;
import com.transmoney.accountservice.dto.AccountResponse;
import com.transmoney.accountservice.dto.CreateAccountRequest;
import com.transmoney.accountservice.repository.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.SecureRandom;

@Service
@Slf4j
@AllArgsConstructor

public class AccountService {
    private final AccountRepository accountRepository;
    private final SecureRandom secureRandom = new SecureRandom();
    public AccountResponse createAccount(CreateAccountRequest request) {
        log.info("Creating an account for : {}" , request.getEmail());

        if(accountRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Account already exists against this email: " + request.getEmail());
        }

        Account account = new Account();
        account.setAccountHolderName(request.getAccountHolderName());
        account.setEmail(request.getEmail());
        account.setPhone(request.getPhone());
        account.setAccountType(request.getAccountType());
        account.setAccountStatus(AccountStatus.ACTIVE);
        account.setBalance(request.getInitialDeposit());
        account.setAccountNumber(generateAccountNumber());
        account.setDailyTransactionLimit(
                request.getAccountType() == AccountType.SAVING
                ? new BigDecimal("100000")
                : new BigDecimal("500000")
        );

        Account savedAccount = accountRepository.save(account);

        log.info("Account created : " + savedAccount.getAccountNumber());

        return mapToResponse(savedAccount);




    }

    // generate unique 12 digits account number :
    private String generateAccountNumber() {
        String accountNumber ;
        do {
            long number = secureRandom.nextLong(1000000000000L);
            accountNumber = String.format("%012d", number);
        } while(accountRepository.existsByAccountNumber(accountNumber));

        return accountNumber;
    }

    private AccountResponse mapToResponse(Account savedAccount) {
        AccountResponse accountResponse = new AccountResponse();
        accountResponse.setId(savedAccount.getId());
        accountResponse.setAccountNumber(savedAccount.getAccountNumber());
        accountResponse.setAccountHolderName(savedAccount.getAccountHolderName());
        accountResponse.setEmail(savedAccount.getEmail());
        accountResponse.setPhone(savedAccount.getPhone());
        accountResponse.setAccountType(savedAccount.getAccountType());
        accountResponse.setAccountStatus(savedAccount.getAccountStatus());
        accountResponse.setBalance(savedAccount.getBalance());
        accountResponse.setDailyTransactionLimit(savedAccount.getDailyTransactionLimit());
        accountResponse.setCreatedAt(savedAccount.getCreatedAt());

        return accountResponse;

    }

    public AccountResponse getAccount(String accountNumber) {

    }
}
