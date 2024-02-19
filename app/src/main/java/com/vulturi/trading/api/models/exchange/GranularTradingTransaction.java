package com.vulturi.trading.api.models.exchange;

import com.vulturi.trading.api.models.transaction.Transaction;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collection;

@Data
public class GranularTradingTransaction {
    private String accountId;
    private String portfolioId;
    private TradingTransaction transaction;
    private TradingFees tradingFees;
    private Collection<Transaction> credits;
    private Collection<Transaction> debits;
}
