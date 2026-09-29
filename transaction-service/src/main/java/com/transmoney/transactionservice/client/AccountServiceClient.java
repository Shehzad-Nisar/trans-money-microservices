package com.transmoney.transactionservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@FeignClient(value = "account-service",url = "${account.service.url")
public interface AccountServiceClient {
    @RequestMapping("/api/v1/accounts/{accountNumber}/deduct")
    String deductBalance(
            @PathVariable String accountNumber,
            @RequestParam BigDecimal amount);
}
