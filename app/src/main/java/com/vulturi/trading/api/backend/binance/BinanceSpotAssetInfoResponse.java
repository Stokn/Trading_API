package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter @Setter
public class BinanceSpotAssetInfoResponse {

    private Collection<BinanceSpotAssetInfoForSubAccount> data;
}
