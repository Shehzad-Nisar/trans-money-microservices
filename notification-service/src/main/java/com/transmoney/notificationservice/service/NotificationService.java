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
    @KafkaListener(
            topics = "otp.required",
            groupId = "notification-service")
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
                             "Suspicious activity detected on your account.%n" +
                                     "Reason: %s%n" +
                                     "A transaction of %s is pending verification.%n" +
                                     "Your OTP is: %s. Valid for 5 minutes.%n" +
                                     "If this wasn't you, please contact your bank immediately.",
                             reason,
                             amount,
                             otpNumber
                     ));


         } catch (Exception e) {

             log.error("Error sending OTP notification: {} ", e.getMessage());
         }

    }


    //Consume transaction.completed event from transaction-service
    @KafkaListener(
            topics = "transaction.completed",
            groupId = "notification-service")
    private void consumeTransactionCompletedEvent(
            @Payload Map<String , Object> completedEvent){

        try {
            String senderAccount = (String) completedEvent.get("senderAccountNumber");
            String receiverAccountNumber = (String) completedEvent.get("receiverAccountNumber");
            String amount = completedEvent.get("amount").toString();

            // DEBIT Alert to sender :
            sendAlert(senderAccount,
                    "DEBIT ALERT",
                    String.format(
                            "%s debited from account %s",
                            amount,senderAccount
                    ));

            // CREDIT Alert to receiver :
            sendAlert(receiverAccountNumber,
                    "CREDIT ALERT",
                    String.format(
                            "%s credited to account %s",
                            amount,receiverAccountNumber
                    ));

        } catch (Exception e) {
            log.error("Error sending transaction complete alert : {} ", e.getMessage());
        }

    }

    //Consume fraud.detected event from transaction-service and sends msg to user.
    @KafkaListener(
            topics = "fraud.detected",
            groupId = "notification-service")
    public void consumeFraudDetectedEvent(
            @Payload Map<String , Object> fraudDetectedPayload){
       try {
           String senderAccountNumber = (String) fraudDetectedPayload.get("senderAccountNumber");
           String reason = (String) fraudDetectedPayload.get("reason");

           // Alert to sender about fraud detected :
           sendAlert(senderAccountNumber,
                   "SUSPICIOUS ACTIVITY DETECTED",
                   String.format("Your account : %s has been blocked.%n" +
                                   "Reason: %s.%n" +
                                   "Please contact your bank immediately.",
                           senderAccountNumber,
                           reason));

       } catch (Exception e) {
           log.error("Error sending fraud alert : {} ", e.getMessage());
       }
    }


    // Consume transaction.refunded from transaction service and alert to sender:
    @KafkaListener(
            topics = "transaction.refunded",
            groupId = "notification-service")
    public void consumeRefundedEvent(
            @Payload Map<String , Object> refundedPayload){

        try {
            String senderAccountNumber = (String) refundedPayload.get("senderAccountNumber");
            String reason = (String) refundedPayload.get("reason");
            String amount = refundedPayload.get("amount").toString();


            // Alert about refunded amount
            sendAlert(senderAccountNumber,
                    "REFUND PROCESSED",
                    String.format("Your transaction of %s was cancelled. %n"+
                                   "Reason: %s.%n" +
                                    "Amount of %s refunded to account %s.",
                                   amount,
                                   reason,
                                   amount,senderAccountNumber));


        } catch (Exception e) {
            log.error("Error sending refund alert : {} ", e.getMessage());
        }
    }

    private void sendAlert(String senderAccountNumber, String subject, String message) {

        log.info("----------------------------------------------------------");
        log.info("Account: {}", senderAccountNumber);
        log.info("Subject: {}", subject);
        log.info("Message: {}", message);
        log.info("----------------------------------------------------------");

    }


}
