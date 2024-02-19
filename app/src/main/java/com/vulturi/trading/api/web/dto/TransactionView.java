package com.vulturi.trading.api.web.dto;

import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionType;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class TransactionView {

    private String accountId;
    private String portfolioId;
    private TransactionSide transactionSide;
    private TransactionType transactionType;
    private String asset;
    private BigDecimal amount;

}
