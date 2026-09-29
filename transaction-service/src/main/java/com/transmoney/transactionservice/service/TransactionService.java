package com.transmoney.transactionservice.service;


import com.transmoney.transactionservice.client.AccountServiceClient;
import com.transmoney.transactionservice.dto.TransactionResponse;
import com.transmoney.transactionservice.dto.TransferReq;
import com.transmoney.transactionservice.entity.Transaction;
import com.transmoney.transactionservice.entity.TransactionStatus;
import com.transmoney.transactionservice.entity.TransactionType;
import com.transmoney.transactionservice.event.TransactionInitiatedEvent;
import com.transmoney.transactionservice.repository.TransactionRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String,Object> kafkaTemplate;

    private final String TRANSACTION_INITIATED_TOPIC = "transaction.initiated";
    private final String TRANSACTION_COMPLETED_TOPIC = "transaction.completed";
    private final String TRANSACTION_REFUNDED_TOPIC = "transaction.refunded";



    /*
    * SAGA STEP -1: Initiate transfer
    * Deduct from sender account via feign
    * Saves transaction as PROCESSING
    * Publish event to Kafka for fraud check
    * Return
    * @param request
    * @return
    * */

    public TransactionResponse transfer(TransferReq req) {
        log.info("SAGA STARTED: Transfer : {} -> {} amount : {}",req.getSenderAccountNumber(), req.getReceiverAccountNumber(),req.getAmount());

        //SAGA STEP 1: deduct form sender account.
        accountServiceClient.deductBalance(
                req.getSenderAccountNumber(),
                req.getAmount());

        Transaction transaction = new Transaction();
        transaction.setSenderAccountNumber(req.getSenderAccountNumber());
        transaction.setReceiverAccountNumber(req.getReceiverAccountNumber());
        transaction.setAmount(req.getAmount());
        transaction.setType(TransactionType.TRANSFER);
        transaction.setStatus(TransactionStatus.PROCESSING);
        transaction.setDescription(req.getDescription());
        transaction.setReferenceNumber(UUID.randomUUID().toString());

        Transaction savedTransaction = transactionRepository.save(transaction);
        log.info("Transaction saved as PROCESSING: {} ",transaction.getId());


        //SAGA STEP 2 -Publish for fraud check
        TransactionInitiatedEvent event = new TransactionInitiatedEvent(
                savedTransaction.getId(),
                savedTransaction.getSenderAccountNumber(),
                savedTransaction.getReceiverAccountNumber(),
                savedTransaction.getAmount(),
                savedTransaction.getDescription());

        kafkaTemplate.send(TRANSACTION_INITIATED_TOPIC,event.getTransactionId(),event);
        log.info("SAGA STEP 2 -> transactionInitiatedEven published : {} ",savedTransaction.getId());


        return mapToResponse(savedTransaction);


    }

    private TransactionResponse mapToResponse(Transaction savedTransaction) {
        TransactionResponse response = new TransactionResponse();
        response.setId(savedTransaction.getId());
        response.setSenderAccountNumber(savedTransaction.getSenderAccountNumber());
        response.setReceiverAccountNumber(savedTransaction.getReceiverAccountNumber());
        response.setAmount(savedTransaction.getAmount());
        response.setType(savedTransaction.getType());
        response.setStatus(savedTransaction.getStatus());
        response.setDescription(savedTransaction.getDescription());
        response.setFailureReason(savedTransaction.getFailureReason());
        response.setReferenceNumber(savedTransaction.getReferenceNumber());
        response.setCreatedAt(savedTransaction.getCreatedAt());
        response.setCompletedAt(savedTransaction.getCompletedAt());

        return response;

    }


    public TransactionResponse getTransaction(String transactionId) {

        return mapToResponse(transactionRepository.findById(transactionId)
                .orElseThrow(()-> new RuntimeException("Transaction Not found.")));

    }

    public List<TransactionResponse> getTransactionHistory(String accountNumber) {

        return transactionRepository.findBySenderAccountNumberOrderByCreatedAtDesc(accountNumber)
                .stream()
                .map(this::mapToResponse)
                .toList();


    }
}
