package com.vulturi.trading.api.models.coin;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CoinLastPrice {
    private BigDecimal eur;
    private BigDecimal usd;
}
