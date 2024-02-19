package com.vulturi.trading.api.services.marketplace;

import com.vulturi.trading.api.models.coin.Coin;

import java.util.Collection;

public interface ConvertService {
    Collection<Coin> getAvailableCoinForTrading(String fromCoin);


    Collection<String> getAvailablePairsForTrading();

    void getTradingPairDetails(String pair);

}
