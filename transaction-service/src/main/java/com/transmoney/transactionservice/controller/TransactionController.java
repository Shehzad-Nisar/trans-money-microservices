package com.transmoney.transactionservice.controller;

import com.transmoney.transactionservice.dto.TransactionResponse;
import com.transmoney.transactionservice.dto.TransferReq;
import com.transmoney.transactionservice.entity.Transaction;
import com.transmoney.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@Slf4j
@RequestMapping("/api/v1/transactions")

public class TransactionController {

    private final TransactionService transactionService;

    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponse> transfer(
            @Payload @Valid TransferReq req){

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionService.transfer(req));
    }

    @GetMapping("/{transactionId}}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @PathVariable String transactionId
    ){

        return ResponseEntity.status(HttpStatus.OK)
                .body(transactionService.getTransaction(transactionId));
    }



}
