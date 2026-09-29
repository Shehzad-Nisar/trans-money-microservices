package com.transmoney.frauddetectionservice.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class FraudDetectionConsumerEvent {

    private final FraudDetectionService fraudDetectionService;


    /*
    * -Listen to Kafka topic : transaction.initiated
    * -Every transaction must go through fraud check before completing.
    * @param payload
    * */
    @KafkaListener(
            topics = "transaction.initiated",
            groupId = "fraud-detection-service")
    public void consumeTransactionInitiated(
            @Payload Map<String,Object> payload){

        log.info("Received transaction for fraud check : {} ", payload.get("transactionId"));

        try {

            fraudDetectionService.check(payload);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
}
