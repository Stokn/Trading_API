package com.vulturi.trading.api.services.marketplace;

import com.vulturi.trading.api.backend.binance.BinanceSymbol;
import com.vulturi.trading.api.backend.binance.BinanceTicker;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.marketplace.TradingPairInfo;
import com.vulturi.trading.api.models.marketplace.ohlc.*;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.services.ohlc.OHLCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MarketPlaceServiceImpl implements MarketPlaceService {


    @Autowired
    private BinanceService binanceService;

    @Autowired
    private ConvertService convertService;


    @Autowired
    private CoinService coinService;

    private List<Frequency> frequencies = List.of(Frequency.HOUR, Frequency.DAY);

    @Override
    public void saveHistorical(String symbol) throws InterruptedException {
        LocalDateTime minTs = LocalDateTime.now(Clock.systemUTC()).minusYears(3);
        Frequency frequency = Frequency.HOUR;
        OHLCFilter filter = OHLCFilter.builder()
                .productCodes(Arrays.asList(symbol))
                .minTs(minTs)
                .frequency(frequency)
                .build();
        Objects.requireNonNull(OHLCService.getServiceForFrequency(frequency)).saveHistoricalDataFrom(filter);


    }


    @Override
    public Collection<TradingPairInfo> getListedPairs() {
        Collection<TradingPairInfo> tradingPairInfoCollection = new ArrayList<>();
        Collection<String> availablePairsForTrading = convertService.getAvailablePairsForTrading();
        for (String tradingPair : availablePairsForTrading) {
            BinanceTicker binanceTicker = binanceService.getBinanceTicker(tradingPair);
            BinanceSymbol symbol = binanceService.getSymbol(tradingPair);
            TradingPairInfo tradingPairInfo = new TradingPairInfo();
            tradingPairInfo.setSymbol(symbol.getSymbol());
            tradingPairInfo.setBaseAsset(symbol.getBaseAsset());
            tradingPairInfo.setQuoteAsset(symbol.getQuoteAsset());
            tradingPairInfo.setLastPrice(binanceTicker.getLastPrice());
            tradingPairInfo.setPctChange24h(binanceTicker.getPriceChangePercent());
            tradingPairInfo.setBaseCoin(coinService.findBySlug(symbol.getBaseAsset()));
            tradingPairInfo.setQuoteCoin(coinService.findBySlug(symbol.getQuoteAsset()));
            tradingPairInfoCollection.add(tradingPairInfo);
        }
        return tradingPairInfoCollection;
    }


    @Override
    public MarketDataClosePrice getOHLC(String symbol) throws ApiException {
        BinanceTicker binanceTicker = binanceService.getBinanceTicker(symbol);
        BinanceSymbol existingSymbol = binanceService.getSymbol(symbol);
        if (binanceTicker == null) {
            throw new ApiException(ApiError.MISSING_VALUE, symbol + "does not exist or is not listed");
        }
        MarketDataClosePrice marketDataOHLC = new MarketDataClosePrice();
        marketDataOHLC.setLastPrice(binanceTicker.getLastPrice());

        marketDataOHLC.setBaseAsset(coinService.findBySlug(existingSymbol.getBaseAsset()));
        marketDataOHLC.setQuoteAsset(coinService.findBySlug(existingSymbol.getQuoteAsset()));
        Collection<OHLC> ohlcData = getOHLCData(Frequency.HOUR, symbol);
        Map<LocalDateTime, BigDecimal> ohlc = ohlcData.stream().collect(Collectors.toMap(o -> o.getPk().getTs(), OHLC::getClose, (bigDecimal, bigDecimal2) -> {
            return bigDecimal;
        }));
        OHLCTimeFrame ohOhlcTimeFrame = toOHOhlcTimeFrame(ohlc);
        marketDataOHLC.setSymbol(symbol);
        marketDataOHLC.setDailyPrices(ohOhlcTimeFrame.getDailyPrices());
        marketDataOHLC.setWeeklyPrices(ohOhlcTimeFrame.getWeeklyPrices());
        marketDataOHLC.setMonthlyPrices(ohOhlcTimeFrame.getMonthlyPrices());
        marketDataOHLC.setAnnualPrices(ohlc);
        return marketDataOHLC;
    }

    private OHLCTimeFrame toOHOhlcTimeFrame(Map<LocalDateTime,BigDecimal> prices){
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime dailyTrigger = now.minusDays(1);
        LocalDateTime weeklyTrigger = now.minusWeeks(1);
        LocalDateTime monthlyTrigger = now.minusMonths(1);

        OHLCTimeFrame ohlcTimeFrame = new OHLCTimeFrame();
        ohlcTimeFrame.setDailyPrices(prices.entrySet().stream().filter(keyValue->keyValue.getKey().isAfter(dailyTrigger)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        ohlcTimeFrame.setWeeklyPrices(prices.entrySet().stream().filter(keyValue->keyValue.getKey().isAfter(weeklyTrigger)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        ohlcTimeFrame.setMonthlyPrices(prices.entrySet().stream().filter(keyValue->keyValue.getKey().isAfter(monthlyTrigger)).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
        ohlcTimeFrame.setAnnualPrices(prices);
        return ohlcTimeFrame;
    }


    private Collection<OHLC> getOHLCData(Frequency frequency, String symbol) {
        Collection<OHLC> data = new ArrayList<>();
        Collection<? extends OHLC> fromCache = Objects.requireNonNull(OHLCService.getServiceForFrequency(frequency)).getFromCache(symbol);
        if (fromCache != null) {
            log.info("{} OHLC found for {} frequency : {}", fromCache.size(), symbol, frequency);
            data.addAll(fromCache);
        }
        return data;
    }


}
