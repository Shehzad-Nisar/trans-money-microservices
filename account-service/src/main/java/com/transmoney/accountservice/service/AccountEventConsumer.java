package com.transmoney.accountservice.service;


import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class AccountEventConsumer {
    private final AccountService accountService;

    /*
    * - Consume transaction.completed event from Kafka.
    * - Credit receiver's Account.
    * @param payload.
    * */
    @KafkaListener(topics = "transaction.completed")
    public void consumeTransactionCompleted(
            @Payload Map<String,Object> payload){
        try {
            String receiverAccount = (String) payload.get("receiverAccountNumber");
            BigDecimal amount = new BigDecimal(payload.get("amount").toString());

            log.info("Crediting Account : {} amount : {}", receiverAccount, amount);
            accountService.creditBalance(receiverAccount,amount);


        } catch (Exception e){
            log.error("Error crediting account : {}", e.getMessage());

        }

    }

    /*
    * -Consume fraud detection event from Kafka .
    * - Blocked that flagged account.
    * - @param payload
    *
    * */

    @KafkaListener(topics = "fraud.detected")
    public void consumeFraudDetected(
            @Payload Map<String, Object> payload){
    try {
        String receiverAccount = (String) payload.get("receiverAccountNumber");
        log.info("Fraud detected now blocking account : {}",receiverAccount);

        accountService.blockAccount(receiverAccount);
    }catch (Exception e){
        log.error("Error blocking account : {}", e.getMessage());

    }
    }
}
