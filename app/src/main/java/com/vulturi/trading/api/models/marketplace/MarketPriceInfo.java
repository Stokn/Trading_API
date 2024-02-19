package com.vulturi.trading.api.models.marketplace;

import com.vulturi.trading.api.backend.binance.BinanceTicker;
import com.vulturi.trading.api.models.coin.Coin;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MarketPriceInfo {
    private String symbol;
    private String baseSlug;
    private BigDecimal prevClosePrice;
    private BigDecimal priceChangePercent;
    private BigDecimal lastPrice;
    private BigDecimal openPrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private String coinLogoUrl;
    private String coinName;
}
