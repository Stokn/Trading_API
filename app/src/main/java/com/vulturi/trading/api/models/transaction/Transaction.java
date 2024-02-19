package com.vulturi.trading.api.models.transaction;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "T_TRANSACTION")
@ToString@Builder@NoArgsConstructor@AllArgsConstructor
public class Transaction {
    @Id
    private String id = UUID.randomUUID().toString();
    private String operationId = UUID.randomUUID().toString();
    private String accountId;
    private String portfolioId; // todo create CRUDE for Portfolio
    private TransactionSide transactionSide;
    private TransactionType transactionType;
    @Column(precision = 27, scale = 10)
    private String asset;
    @Column(precision = 27, scale = 10)
    private BigDecimal amount;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
}