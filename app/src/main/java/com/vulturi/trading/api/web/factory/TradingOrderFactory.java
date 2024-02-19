package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.order.TradingOrder;
import com.vulturi.trading.api.web.dto.TradingOrderView;

public class TradingOrderFactory {
    public static TradingOrder tradingOrder(TradingOrderView tradingOrderView){
        TradingOrder tradingOrder = new TradingOrder();
        tradingOrder.setFromAsset(tradingOrderView.getFrom());
        tradingOrder.setToAsset(tradingOrderView.getTo());
        tradingOrder.setAccountId(tradingOrderView.getAccountId());
        tradingOrder.setQuantity(tradingOrderView.getQuantity());
        tradingOrder.setExchange(ExchangePlatform.BINANCE);
        tradingOrder.setSchedule(false);
        return tradingOrder;
    };
}
