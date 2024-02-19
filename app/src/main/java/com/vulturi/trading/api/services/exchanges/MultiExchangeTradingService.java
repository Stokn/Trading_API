package com.vulturi.trading.api.services.exchanges;


import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.order.TradingOrder;
import com.vulturi.trading.api.models.user.Account;

import java.util.Collection;

public interface MultiExchangeTradingService {

     Collection<TradingTransaction> placeOrder(Account account, TradingOrder order) throws ApiException;

}
