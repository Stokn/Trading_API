package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class BinanceSpotAssetInfoForSubAccount {

    private String subAccountId;
    private BigDecimal totalBalanceOfBtc;
}
