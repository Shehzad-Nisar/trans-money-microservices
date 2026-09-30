package com.transmoney.frauddetectionservice.service;


import com.transmoney.frauddetectionservice.service.client.AccountServiceClient;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class FraudDetectionService {
    private final AccountServiceClient accountServiceClient;
    public void check(Map<String, Object> payload) {
        String transactionId = (String) payload.get("transactionId");
        String senderAccountNumber = (String) payload.get("senderAccountNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString()) ;


        //Fetching real balance data from account-service.
        BigDecimal senderBalance = accountServiceClient.getBalance(senderAccountNumber);

    }
}
