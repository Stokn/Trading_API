package com.vulturi.trading.api.services.marketplace;

import com.vulturi.trading.api.backend.binance.BinanceSymbol;
import com.vulturi.trading.api.backend.binance.BinanceTicker;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.marketplace.MarketPriceInfo;

import java.math.BigDecimal;
import java.util.Collection;

public interface BinanceService {


    BinanceTicker getBinanceTicker(String symbol);

    BigDecimal getLastPriceEUR(String symbol);

    Collection<MarketPriceInfo> getTickersEUR();

    boolean canTrade(String symbol);

    BinanceSymbol getSymbol(String symbol);
    Collection<BinanceSymbol> getAllSymbols();

    Coin getAvailableCoinForTrading(String fromCoin,String toCoin);

    Coin getTradingPairs(String fromCoin, String toCoin);




}
