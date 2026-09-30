package com.transmoney.frauddetectionservice.service;


import com.transmoney.frauddetectionservice.client.AccountServiceClient;
import com.transmoney.frauddetectionservice.model.FraudCheckResult;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@AllArgsConstructor
public class FraudDetectionService {
    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final RedisTemplate<String,String> redisTemplate;

    @Value("${fraud.max-transaction-per-minute}")
    private final int maxTransactionsPerMinute;


    private static final String VERIFICATION_REQUIRED_TOPIC = "verification.required";
    private static final String FRAUD_CHECK_CLEAN_RESULT_TOPIC = "fraud.check.clean";



    public void check(Map<String, Object> payload) {
        String transactionId = (String) payload.get("transactionId");
        String senderAccountNumber = (String) payload.get("senderAccountNumber");
        BigDecimal amount = new BigDecimal(payload.get("amount").toString()) ;


        //Fetching real balance data from account-service.
        BigDecimal senderBalance = accountServiceClient.getBalance(senderAccountNumber);

        log.info("Checking transaction: {} account : {} amount : {} senderBalance : {} ",transactionId,senderAccountNumber,amount,senderBalance);

        FraudCheckResult fraudCheckResult =  performFraudCheck(senderAccountNumber,amount,senderBalance);
        if(fraudCheckResult.isFraud()){
            log.info("Suspicious activity detected in account : {}" + "Reason : {} requesting OTP verification.",senderAccountNumber,fraudCheckResult.getReason());

            Map<String,Object> verificationEvent = new HashMap<>();
            verificationEvent.putIfAbsent("transactionId",transactionId);
            verificationEvent.put("accountNumber",senderAccountNumber);
            verificationEvent.put("amount",amount);
            verificationEvent.put("reason",fraudCheckResult.getReason());

            //sending event to kafka template so other service can consume it :
            kafkaTemplate.send(VERIFICATION_REQUIRED_TOPIC,transactionId,verificationEvent);

        }else {
            log.info("Transaction is clean.");
            Map<String ,Object> transactionCleanEvent = new HashMap<>();
            transactionCleanEvent.put("transactionId",transactionId);
            transactionCleanEvent.put("isFraud",false);
            transactionCleanEvent.put("reason",null);

            kafkaTemplate.send(FRAUD_CHECK_CLEAN_RESULT_TOPIC,transactionId,transactionCleanEvent);

        }
    }

    private FraudCheckResult performFraudCheck(
            String senderAccountNumber,
            BigDecimal amount,
            BigDecimal senderBalance) {

        //Patten no 1 : Velocity check
        if(isVelocityExceeded(senderAccountNumber)){
            return new FraudCheckResult(true,"Too many transactions in 60 seconds." + "-Velocity limit exceeded.");
        }

        //Pattern no 2 : Amount check
        if(isAmountSuspicious(senderAccountNumber,amount)){

            return new FraudCheckResult(true,"Unusual transaction amount."+"-Exceeds 3x your usual transaction amount.");
        }

        //Pattern no 3 : Balance check
        if(senderBalance.compareTo(BigDecimal.ZERO)>0 && isBalanceCheckFailed(senderBalance,amount)){

            return new FraudCheckResult(true,"Transaction exceeds 90% of your account balance.");

        }

    }

    private boolean isVelocityExceeded(String senderAccountNumber) {
        String key = "fraud-velocity"+senderAccountNumber;
        Long count = redisTemplate.opsForValue().increment(key);

        if(count != null && count ==1){
            redisTemplate.expire(key, Duration.ofSeconds(60));

        }

        return count != null && count>maxTransactionsPerMinute;
    }
}
