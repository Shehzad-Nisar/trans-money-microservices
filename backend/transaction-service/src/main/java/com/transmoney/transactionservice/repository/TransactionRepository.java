package com.transmoney.transactionservice.repository;

import com.transmoney.transactionservice.dto.TransactionResponse;
import com.transmoney.transactionservice.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction,String> {
    List<Transaction> findBySenderAccountNumberOrderByCreatedAtDesc(String accountNumber);
    List<Transaction> findBySenderAccountNumberOrReceiverAccountNumberOrderByCreatedAtDesc(String sender, String receiver);
}
