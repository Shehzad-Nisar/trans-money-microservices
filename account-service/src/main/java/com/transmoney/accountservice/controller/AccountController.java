package com.transmoney.accountservice.controller;


import com.transmoney.accountservice.Entity.Account;
import com.transmoney.accountservice.service.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/accounts")
@Slf4j
@AllArgsConstructor

public class AccountController {
    private final AccountService accountService;
}
