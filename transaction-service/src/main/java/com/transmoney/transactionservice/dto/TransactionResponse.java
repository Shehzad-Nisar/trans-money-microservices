package com.transmoney.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransactionResponse {


    @NotBlank(message = "SENDER ACCOUNT NUMBER IS REQUIRED.")
    private String senderAccountNumber;

    @NotBlank(message = "RECEIVER ACCOUNT NUMBER IS REQUIRED.")
    private String receiverAccountNumber;

    @NotNull(message = "TRANSFER AMOUNT SHOULD NOT BE NULL.")
    @Positive(message = "AMOUNT SHOULD BE POSITIVE.")
    private BigDecimal amount;

    private String description;
}
