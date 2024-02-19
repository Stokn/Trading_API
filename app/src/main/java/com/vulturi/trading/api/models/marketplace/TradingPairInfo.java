package com.vulturi.trading.api.models.marketplace;

import com.vulturi.trading.api.models.coin.Coin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradingPairInfo {
    private String symbol;
    private String baseAsset;
    private String quoteAsset;
    private Coin baseCoin;
    private Coin quoteCoin;
    private BigDecimal lastPrice;
    private BigDecimal pctChange24h;
}
