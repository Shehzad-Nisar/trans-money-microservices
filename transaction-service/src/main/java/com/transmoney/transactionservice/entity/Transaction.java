package com.transmoney.transactionservice.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "transactions")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id ;

    String senderAccountNumber;

    String receiverAccountNumber;

    BigDecimal amount;


}
