package com.vulturi.trading.api.models.marketplace.ohlc;

import com.vulturi.trading.api.models.coin.Coin;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@ToString
public class MarketDataClosePrice {
    private Coin baseAsset;
    private Coin quoteAsset;
    private String symbol;
    private BigDecimal lastPrice;
    private Map<LocalDateTime, BigDecimal> dailyPrices;
    private Map<LocalDateTime, BigDecimal> weeklyPrices;
    private Map<LocalDateTime, BigDecimal> monthlyPrices;
    private Map<LocalDateTime, BigDecimal> annualPrices;
}
