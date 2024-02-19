package com.vulturi.trading.api.services.exchanges;


import com.vulturi.exchanges.connector.service.ExchangeConnector;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.order.TradingOrder;

public interface TradingService {

    TradingTransaction trade(TradingOrder tradingOrder) throws ApiException;

    ExchangeConnector getExchangeConnector();



}
