package com.vulturi.trading.api.backend.binance;

import lombok.*;

@Builder @AllArgsConstructor @NoArgsConstructor
@Getter @Setter @ToString
public class BinanceSubAccountCreationResponse {
    private String subaccountId;
    private String email;
    private String tag;
}
