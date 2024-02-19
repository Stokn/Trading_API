package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

@Getter @Setter
public class BinanceSymbol {

    private String symbol;
    private Collection<Map<String, String>> filters;
    private String baseAsset;
    private String quoteAsset;
    private String status;
    private BigDecimal minSize;
    private BigDecimal stepSize;
    private BigDecimal minNotional;
    private String pairName;

    @Override
    public String toString() {
        return "BinanceSymbol{" +
                "symbol='" + symbol + '\'' +
                ", baseAsset='" + baseAsset + '\'' +
                ", quoteAsset='" + quoteAsset + '\'' +
                ", status='" + status + '\'' +
                ", minSize=" + minSize +
                ", stepSize=" + stepSize +
                ", minNotional=" + minNotional +
                ", pairName='" + pairName + '\'' +
                '}';
    }
}
