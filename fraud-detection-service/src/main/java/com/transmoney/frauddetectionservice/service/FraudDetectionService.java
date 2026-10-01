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
import java.math.RoundingMode;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Service
@Slf4j
@AllArgsConstructor
public class FraudDetectionService {
    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String,Object> kafkaTemplate;
    private final RedisTemplate<String,String> redisTemplate;

    @Value("${fraud.max-transaction-per-minute}")
    private final int maxTransactionsPerMinute;

    @Value("${fraud.suspicious-amount-multiplier}")
    private final double suspiciousAmountMultiplier;

    @Value("${fraud.max-balance-percentage}")
    private final double maxBalancePercentage;


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

        return new FraudCheckResult(false,"No reason.");

    }


    // IMPLEMENTING THREE PATTERNS LOGICS ONE BY ONE.
    // 1 : isVelocityExceeded().
    private boolean isVelocityExceeded(String senderAccountNumber) {
        String key = "fraud-velocity"+senderAccountNumber;
        Long count = redisTemplate.opsForValue().increment(key);

        if(count != null && count ==1){
            redisTemplate.expire(key, Duration.ofSeconds(60));

        }

        return count != null && count>maxTransactionsPerMinute;
    }

    // 2 : isAmountSuspicious().
    private boolean isAmountSuspicious(String senderAccountNumber, BigDecimal amount) {

        //Make redis to store the avg_amount of transactions.
        String avg_key = "fraud:avg_amount"+senderAccountNumber;

        //whenever a transaction happened value from redis will store to this "avg_Str_amount".
        String avg_Str_amount = redisTemplate.opsForValue().get(avg_key);

        //It will run when it is the first transaction.
        if(avg_Str_amount == null){
            redisTemplate.opsForValue().set(avg_key,amount.toString());
            avg_Str_amount = amount.toString();
        }

        /*
        * 1- Setting the Average amount in Big Decimal for setting the threshold value
        * 2- threshold will be = averageAmount X suspiciousAmountMultiplier
        *    -> 1000 X 5 = 5000 will be threshold in this case.
        *
        * */
        BigDecimal avgAmount = new BigDecimal(avg_Str_amount);
        BigDecimal threshold = avgAmount.multiply(BigDecimal.valueOf(suspiciousAmountMultiplier));

        // update the running average.
        BigDecimal new_avg = avgAmount.add(amount).divide(BigDecimal.valueOf(2),2, RoundingMode.HALF_UP);

        //updating average in redis.
        redisTemplate.opsForValue().set(avg_key,new_avg.toString());

        log.info("Amount check -amount : {} threshold : {} suspicious : {} ",amount,threshold,amount.compareTo(threshold)> 0);

        //returning the result in boolean if suspicious it will return 'TRUE' else 'FALSE'.:
        return amount.compareTo(threshold)>0;
    }

    //3. isBalanceCheckFailed(BigDecimal senderBalance, BigDecimal amount)

    private boolean isBalanceCheckFailed(BigDecimal senderBalance, BigDecimal amount) {

        BigDecimal maxAllowedBalance = senderBalance.multiply(
                BigDecimal.valueOf(maxBalancePercentage));

        log.info(
                "Balance check -> amount : {} maxAllowedBalance : {} IsSuspicious : {} ",
                amount,maxAllowedBalance,amount.compareTo(maxAllowedBalance)> 0
                );

        return amount.compareTo(maxAllowedBalance)>0;

    }
}
