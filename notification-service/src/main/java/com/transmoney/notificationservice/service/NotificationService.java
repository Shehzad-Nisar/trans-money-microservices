package com.transmoney.notificationservice.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;


@Service
@Slf4j
public class NotificationService {

   //Consume otp.required event from transaction-service
    @KafkaListener(topics = "otp.required",groupId = "notification-service")
    public void otpEventConsumer(
            @Payload Map<String , Object> otpEvent){

         try {

             String transactionId = (String) otpEvent.get("transactionId");
             String senderAccountNumber = (String) otpEvent.get("senderAccountNumber");
             String reason = (String) otpEvent.get("reason");
             String otpNumber = (String) otpEvent.get("otpNumber");
             BigDecimal amount = new BigDecimal(otpEvent.get("amount").toString());

             sendAlert(senderAccountNumber,
                     "TRANSACTION VERIFICATION REQUIRED",
                     String.format(
                             "Suspicious activity detected on you account." +
                             "Reason : %s" +
                             "A transaction of %s is pending verification."+
                             "Your OTP is : %s. Valid for 5 minutes."+
                             "If this wasn't you -ignore this message.",
                             reason,
                             amount,
                             otpNumber
                     ));


         } catch (Exception e) {

             log.error("Error sending OTP notification: {} ", e.getMessage());
         }

    }

    private void sendAlert(String senderAccountNumber, String subject, String message) {

        log.info("Sending notification to account: {}", senderAccountNumber);
        log.info("Subject: {}", subject);
        log.info("Message: {}", message);

    }


}
