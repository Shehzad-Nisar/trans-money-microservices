package com.transmoney.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@FeignClient(value = "account-service",url = "${account.service.url}")
public interface AccountServiceClient {
    @RequestMapping("/api/v1/accounts/{accountNumber}/deduct")
    String deductBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount);


    @RequestMapping("/api/v1/accounts/{accountNumber}/credit")

    String creditBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount);




}
