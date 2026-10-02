package com.transmoney.transactionservice.service;

import com.transmoney.transactionservice.entity.Transaction;
import com.transmoney.transactionservice.entity.TransactionStatus;
import com.transmoney.transactionservice.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionEventConsumer  {
    private final TransactionRepository transactionRepository;
    private final static long OTP_MAX_MINUTES = 5;

    private final RedisTemplate<String,String> redisTemplate;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    private static final String OTP_REQUIRED_TOPIC = "otp.required";


    @KafkaListener(
            topics = "verification.required",
            groupId = "transaction-service")



    /*
    * - CONSUME verification.required FROM FRAUD DETECTION SERVICE
    * - GENERATE OPT AND ASK USERS TO VERIFY.
    * @param payload.
    *
    * */
    public void consumeVerificationRequired(
            @Payload Map<String,Object> verificationEvent){

        try {

            String transactionId = (String) verificationEvent.get("transactionId");
            String senderAccountNumber = (String) verificationEvent.get("senderAccountNumber");
            String reason = (String) verificationEvent.get("reason");
            BigDecimal amount = new BigDecimal(verificationEvent.get("amount").toString());


            log.info(
                    "Verification required -> transaction: {} reason : {} ",
                    transactionId,reason);

            //Checking transaction_DB to extract the transaction if it exits.
            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(()-> new RuntimeException("Transaction not found " + transactionId));

            /*
            * -if transaction has other status except "PROCESSING".
            * -Then shouldn't generate OTP and return or exit.
            * */
            if(transaction.getStatus()!= TransactionStatus.PROCESSING){
                log.info("Transaction -> {} not PROCESSING - Skipping",transactionId);
                return;
            }

            /*
             * -Change the status of transaction to Pending from processing
             * -Then save the changes to DB as well.
             * */
            transaction.setStatus(TransactionStatus.PENDING_VERIFICATION);
            transactionRepository.save(transaction);


            //Generate 6 digits OTP.
            String otpNumber = String.format("%06d",(int) (Math.random()*900000 + 100000));

           //Store opt in Redis -Which will be expired in 5 minutes.
            String otp_key = "verification-otp:"+transactionId;

            redisTemplate.opsForValue().set(otp_key,otpNumber);
            redisTemplate.expire(otp_key, Duration.ofMinutes(OTP_MAX_MINUTES));



            log.info("OTP generated for transaction : {} Will be expire in {} minutes.",
                    transactionId,OTP_MAX_MINUTES);

            //Verify User from User so we need to send an event in Kafka :
           Map<String , Object> notificationEvent = new HashMap<>();
           notificationEvent.put("transactionId",transactionId);
           notificationEvent.put("senderAccountNumber",senderAccountNumber);
           notificationEvent.put("amount",amount);
           notificationEvent.put("reason",reason);
           notificationEvent.put("otpNumber",otpNumber);

           kafkaTemplate.send(OTP_REQUIRED_TOPIC,transactionId,notificationEvent);












        } catch (Exception e) {
            throw new RuntimeException(e);
        }







    }



}
