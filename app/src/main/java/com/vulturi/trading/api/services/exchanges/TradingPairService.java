package com.vulturi.trading.api.services.exchanges;

import com.vulturi.trading.api.models.exchange.TradingPair;

import java.util.List;

public interface TradingPairService {

    List<TradingPair> get(String pair);

    TradingPair filter();

    TradingPair saveOrUpdate(TradingPair tradingPair);

}
