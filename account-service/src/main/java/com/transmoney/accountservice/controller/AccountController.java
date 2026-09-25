package com.transmoney.accountservice.controller;


import com.transmoney.accountservice.Entity.Account;
import com.transmoney.accountservice.dto.AccountResponse;
import com.transmoney.accountservice.dto.CreateAccountRequest;
import com.transmoney.accountservice.service.AccountService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
@AllArgsConstructor

public class AccountController {
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<AccountResponse> createAccount(
            @Valid @RequestBody CreateAccountRequest request)
    {
        return  ResponseEntity.status(HttpStatus.CREATED)
                .body(accountService.createAccount(request));
    }

    @GetMapping("/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(
            @PathVariable String accountNumber){

        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getAccount(accountNumber);
    }

    @GetMapping("/{accountNumber}/balance")
    public ResponseEntity<BigDecimal> getBalance(
            @PathVariable String accountNumber){

        return ResponseEntity.status(HttpStatus.OK)
                .body(accountService.getBalance(accountNumber);
    }

    @PutMapping("/{accountNumber}/block")
    public ResponseEntity<String> blockAccount(
            @PathVariable String accountNumber){
        accountService.blockAccount(accountNumber);
        ResponseEntity.ok("ACCOUNT BLOCKED SUCCESSFULLY.");
    }

    /*
    * SAGA Step 1 - Deduct Balance
    * Called by Transaction Service whenn transfer is initiated.
    * */

    @PutMapping("/{accountNumber}/deduct")
    public ResponseEntity<String> deductBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount){

        accountService.deductBalance(accountNumber,amount);
        return ResponseEntity.ok("AMOUNT DEDUCTED SUCCESSFULLY");
    }

    /*
    * SAGA Step 4: compensating transaction endpoints.
    * CALLED BY TRANSACTION SERVICE IN TWO SCENARIOS.
    * 1- Fraud detected -> refund sender (undo step 1)
    * 2- Transaction completed -> credit receiver's account
    * */

    @PutMapping("/{accountNumber}/credit")
    public ResponseEntity<String> creditBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount){

        accountService.creditBalance(accountNumber, amount);
        return ResponseEntity.ok("BALANCE CREDITED SUCCESSFULLY.")
    }
















}
