package com.vulturi.trading.api.models.exchange;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


import java.math.BigDecimal;

@Data@Entity
@Table(name = "T_TRADING_FEES")
public class TradingFees {
    @Id
    private String tradingTransactionId;
    private String originAccountId;
    private String originPortfolioId;
    private String asset;
    private BigDecimal quantity;
    private BigDecimal valueInEUR;
}
