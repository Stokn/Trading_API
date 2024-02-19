package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter @Setter@ToString
public class BinanceUniversalTransfer {
    private String fromId;
    private String toId;
    private BinanceWallet fromAccountType;
    private BinanceWallet toAccountType;
    private String asset;
    private BigDecimal amount;
}
