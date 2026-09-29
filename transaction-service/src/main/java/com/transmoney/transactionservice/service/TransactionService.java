package com.transmoney.transactionservice.service;


import com.transmoney.transactionservice.dto.TransactionResponse;
import com.transmoney.transactionservice.dto.TransferReq;
import com.transmoney.transactionservice.entity.Transaction;
import com.transmoney.transactionservice.repository.TransactionRepository;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Data
@AllArgsConstructor
@Slf4j
@Service
public class TransactionService {
    private final TransactionRepository transactionRepository;

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


    }



}
