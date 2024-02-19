package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BinanceToMasterTransfer {
    private String fromId;
    private BinanceWallet fromAccountType;
    private BinanceWallet toAccountType;
    private String asset;
    private BigDecimal amount;

}
