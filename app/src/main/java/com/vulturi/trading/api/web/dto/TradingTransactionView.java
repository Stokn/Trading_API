package com.vulturi.trading.api.web.dto;

import com.vulturi.trading.api.converters.ListOfStringConverter;
import com.vulturi.trading.api.models.transaction.Transaction;
import lombok.Data;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;
@Data
public class TradingTransactionView {
    private String accountId;
    private String portfolioId;
    private LocalDateTime ts = LocalDateTime.now(Clock.systemUTC());
    private String orderId;
    private Collection<Transaction> transactions;
    private String fromCoin;
    private String toCoin;
    private String id = UUID.randomUUID().toString();
}
