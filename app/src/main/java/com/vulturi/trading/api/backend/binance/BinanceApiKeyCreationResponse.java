package com.vulturi.trading.api.backend.binance;

import lombok.*;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class BinanceApiKeyCreationResponse {

    private String subaccountId;
    private boolean canTrade;
    private boolean marginTrade;
    private boolean futuresTrade;
    private String apiKey;
    private String secretKey;
}
