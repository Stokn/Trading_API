package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class BinanceTicker {

    private String symbol;
    private BigDecimal prevClosePrice;
    private BigDecimal priceChangePercent;
    private BigDecimal weightedAvgPrice;
    private BigDecimal lastPrice;
    private BigDecimal lastQty;
    private BigDecimal bidPrice;
    private BigDecimal bidQty;
    private BigDecimal askPrice;
    private BigDecimal askQty;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private BigDecimal volume;
    private BigDecimal quoteVolume;
    private Long openTime;
    private Long closeTime;
    private Long firstId;
    private Long lastId;
    private Long count;
}
