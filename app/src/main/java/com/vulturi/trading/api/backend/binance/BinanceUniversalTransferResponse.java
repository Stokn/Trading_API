package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter@ToString
public class BinanceUniversalTransferResponse {

    private String txnId;
    private String clientTranId;

}
