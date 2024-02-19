package com.vulturi.trading.api.models.marketplace.ohlc;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter@Setter@Accessors(chain = true)
public class MultiCcyMarketDataOHLC {
    private String coin;
    private MarketDataClosePrice eur = new MarketDataClosePrice();
    private MarketDataClosePrice usd = new MarketDataClosePrice();
}
