package com.vulturi.trading.api.services.marketplace;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.marketplace.MarketPriceInfo;
import com.vulturi.trading.api.models.marketplace.TradingPairInfo;
import com.vulturi.trading.api.models.marketplace.ohlc.MarketDataClosePrice;

import java.util.Collection;

public interface MarketPlaceService {


    MarketDataClosePrice getOHLC(String symbol) throws ApiException;



    Collection<TradingPairInfo> getListedPairs();



    void saveHistorical(String symbol) throws InterruptedException;


}
