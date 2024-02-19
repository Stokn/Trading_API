package com.vulturi.trading.api.models.deposit;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter@Setter@NoArgsConstructor@AllArgsConstructor
public class ExchangeDeposit {
    private String accountId;
    private String portfolioId;
    private LocalDateTime ts;
    private String txId;
    private String asset;
    private String network;
    private String address;
    private BigDecimal amount;
}
