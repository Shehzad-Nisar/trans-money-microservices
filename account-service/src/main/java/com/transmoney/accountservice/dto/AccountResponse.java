package com.transmoney.accountservice.dto;

import com.transmoney.accountservice.Entity.AccountStatus;
import com.transmoney.accountservice.Entity.AccountType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountResponse {

    private String id;
    private String accountNumber ;
    private String accountHolderName;
    private String email;
    private String phone;
    private AccountType accountType;
    private AccountStatus accountStatus;
    private BigDecimal balance;
    private BigDecimal dailyTransactionLimit;
    private LocalDateTime createdAt;

}
