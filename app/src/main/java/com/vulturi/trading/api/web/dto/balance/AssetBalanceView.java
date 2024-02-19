package com.vulturi.trading.api.web.dto.balance;

import com.vulturi.trading.api.models.coin.Coin;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AssetBalanceView {
    private String portfolioId;
    private LocalDateTime ts;
    private Coin asset;
    private BigDecimal quantity;
    private BigDecimal eurValue;
    private BigDecimal usdValue;
}
