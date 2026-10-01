package com.transmoney.transactionservice.service;

import com.transmoney.transactionservice.entity.Transaction;
import com.transmoney.transactionservice.entity.TransactionStatus;
import com.transmoney.transactionservice.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class TransactionEventConsumer  {
    private final TransactionRepository transactionRepository;
    private final RedisTemplate<String,String> redisTemplate;


    @KafkaListener(
            topics = "verification.required",
            groupId = "transaction-service")
    public void consumeVerificationRequired(
            @Valid @Payload Map<String,Object> verificationEvent){

        try {
            String transactionId = (String) verificationEvent.get("transactionId");
//            String senderAccountNumber = (String) verificationEvent.get("senderAccountNumber");
//            String amount = (String) verificationEvent.get("amount");
//           String reason = (String) verificationEvent.get("reason");

            Transaction transaction = transactionRepository.findById(transactionId)
                    .orElseThrow(()-> new RuntimeException("Transaction not found in transaction DB."));
            transaction.setStatus(TransactionStatus.PENDING_VERIFICATION);

            transactionRepository.save(transaction);

            //6 DIGITS OTP GENERATE

           int randomNumber = (int) (Math.random()*899999 + 100000);
           String optNumber = String.format("%06d",randomNumber);

           //save to Redis along with the keys:
            String opt_key = "opt:generated"+transactionId;
            redisTemplate.opsForValue().set(opt_key,optNumber);







        } catch (Exception e) {
            throw new RuntimeException(e);
        }







    }



}
