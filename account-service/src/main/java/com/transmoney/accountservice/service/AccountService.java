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


    /*
    * Create a new Account.
    *
    * */
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

    /*
    * get Account , called by using account number .
    * */
    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new RuntimeException("Account Not found!."));

        return mapToResponse(account);
    }

    /*
    * Get account balance , called from controller using accNumber:
    * */
    public BigDecimal getBalance(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new RuntimeException("Account NOt Found."));
        return account.getBalance();
    }


/*
* Block Account - Called by Fraud detection Service via Kafka
* */
    public void blockAccount(String accountNumber) {

        log.info("Blocking the Account : {}", accountNumber);
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(()-> new RuntimeException("Account NOt Found."));
        account.setAccountStatus(AccountStatus.BLOCKED);

        accountRepository.save(account);
        log.info("Account Blocked Successfully : {}", accountNumber);


    }
}
