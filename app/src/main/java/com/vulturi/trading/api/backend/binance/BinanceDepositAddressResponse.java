package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BinanceDepositAddressResponse {
    private String coin;
    private String network;
}
