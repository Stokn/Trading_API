package com.vulturi.trading.api.services.exchanges;


import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.order.TradingOrder;

public class KrakenExchangeService extends AbstractExchangeService implements TradingService {


    @Override
    public TradingTransaction trade(TradingOrder tradingOrder) {
        return null;
    }

    @Override
    protected ExchangePlatform getExchangePlatform() {
        return ExchangePlatform.KRAKEN;
    }
}
