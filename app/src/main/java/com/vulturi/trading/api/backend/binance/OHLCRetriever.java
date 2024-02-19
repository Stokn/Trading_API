package com.vulturi.trading.api.backend.binance;

import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLC;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLCSource;

import java.time.LocalDateTime;
import java.util.Collection;

public interface OHLCRetriever {

    OHLC retrieveFromBackend(Frequency frequency, BinanceSymbol symbol, LocalDateTime ts);

    Collection<OHLC> retrieveHistoricalFromBackend(Frequency frequency, BinanceSymbol symbol, LocalDateTime ts) throws InterruptedException;

    OHLCSource getSource();
}