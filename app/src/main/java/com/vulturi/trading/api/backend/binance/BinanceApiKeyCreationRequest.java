package com.vulturi.trading.api.backend.binance;

import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Builder
@Getter @Setter
public class BinanceApiKeyCreationRequest {

    private String subAccountId;
    private boolean canTrade;
}
