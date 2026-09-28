package com.transmoney.transactionservice.entity;



/*
*  TRANSACTION LIFECYCLE FLOW:
*     PENDING->PROCESSING->COMPLETED (clean transaction)
*                    ->PENDING_VERIFICATION(if suspicious found)
*                        ->COMPLETED( if verified)
*                        ->FLAGGED (SAGA Refund)
*                    ->FAILED
*                    ->FLAGGED
* */

public enum TransactionStatus {
    PENDING,
    PROCESSING,
    PENDING_VERIFICATION,
    COMPLETED,
    FAILED,
    FLAGGED
}
