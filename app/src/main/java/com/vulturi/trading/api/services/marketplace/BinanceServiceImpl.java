package com.vulturi.trading.api.services.marketplace;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import com.vulturi.trading.api.backend.binance.BinanceSymbol;
import com.vulturi.trading.api.backend.binance.BinanceTicker;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.marketplace.MarketPriceInfo;
import com.vulturi.trading.api.services.coin.CoinService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BinanceServiceImpl implements BinanceService {

    @Autowired
    private CoinService coinService;

    @Value("${coins}")
    private Collection<String> symbols;
    private Map<String, BinanceTicker> tickerCache = Maps.newConcurrentMap();
    private Map<String, BinanceSymbol> symbolsCache = Maps.newConcurrentMap();
    private Map<String, MarketPriceInfo> marketPriceInfoCache = Maps.newConcurrentMap();
    private Map<String, BigDecimal> lastPriceEur = Maps.newConcurrentMap();
    private OkHttpClient okHttpClient = new OkHttpClient();
    private ObjectMapper objectMapper;

    private String binanceBaseUrl = "https://api.binance.com/api/";

    @PostConstruct
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        refreshCache();
    }

    @Scheduled(cron = "@hourly")
    public void refreshCache() {
        try {
            refreshTickersCache();
        } catch (Exception e) {
            log.error("Error refreshing binance tickers", e);
        }
        try {
            refreshSymbolsCache();
        } catch (Exception e) {
            log.error("Error refreshing binance symbols", e);
        }
    }

    @Scheduled(fixedDelay = 10000)
    private void refreshTickersCache() throws IOException {
        log.info("Refreshing Binance Tickers");
        String url = binanceBaseUrl + "v3/ticker/24hr";
        log.info("Calling url {}", url);
        Request getTickersRequest = new Request.Builder().url(url).get().build();
        Response tickersResponse = okHttpClient.newCall(getTickersRequest).execute();
        if (!tickersResponse.isSuccessful()) {
            throw new RuntimeException("Error is " + tickersResponse.code() + " - " + tickersResponse.message());
        }
        tickerCache = Arrays.stream(objectMapper.readValue(tickersResponse.body().string(), BinanceTicker[].class)).collect(Collectors.toMap(BinanceTicker::getSymbol, t -> t));
        Collection<MarketPriceInfo> marketPriceInfoCollection = getTickersEUR();
        marketPriceInfoCache = marketPriceInfoCollection.stream().collect(Collectors.toMap(MarketPriceInfo::getSymbol, marketPriceInfo -> marketPriceInfo));
        lastPriceEur = marketPriceInfoCollection.stream().collect(Collectors.toMap(MarketPriceInfo::getBaseSlug, MarketPriceInfo::getLastPrice));
        log.info("Binance market data have been refreshed");
    }

    private void refreshSymbolsCache() throws IOException {
        log.info("Refreshing Binance Symbol");
        Request getSymbolsRequest = new Request.Builder().url(binanceBaseUrl + "v3/exchangeInfo").get().build();
        Response symbolsResponse = okHttpClient.newCall(getSymbolsRequest).execute();
        if (!symbolsResponse.isSuccessful()) {
            throw new RuntimeException("Error is " + symbolsResponse.code() + " - " + symbolsResponse.message());
        }
        JsonNode responseRootNode = objectMapper.readTree(symbolsResponse.body().string());
        symbolsCache = Arrays.stream(objectMapper.treeToValue(responseRootNode.get("symbols"), BinanceSymbol[].class))
                .map(s -> {
                            s.getFilters().forEach(f -> {
                                if (f.get("filterType").equalsIgnoreCase("LOT_SIZE")) {
                                    s.setMinSize(new BigDecimal(f.get("minQty")));
                                    s.setStepSize(new BigDecimal(f.get("stepSize")));
                                } else if (f.get("filterType").equalsIgnoreCase("MIN_NOTIONAL")) {
                                    s.setMinNotional(new BigDecimal(f.get("minNotional")));

                                }
                            });
                            return s;
                        }
                ).collect(Collectors.toMap(BinanceSymbol::getSymbol, t -> t));
        log.info("Binance Tickers have been refreshed");
    }


    public BinanceTicker getBinanceTicker(String symbol) {
        return tickerCache.get(symbol);
    }


    public BinanceSymbol getSymbol(String symbol) {
        return symbolsCache.get(symbol);
    }

    public Collection<BinanceSymbol> getAllSymbols() {
        return symbolsCache.values();
    }


    public Collection<MarketPriceInfo> getLastMarketPriceInfoEUR() {
        return marketPriceInfoCache.values();
    }

    public BigDecimal getLastPriceEUR(String symbol) {
        return lastPriceEur.get(symbol);
    }

    public Collection<MarketPriceInfo> getTickersEUR() {
        Collection<MarketPriceInfo> marketPriceInfoCollection = new ArrayList<>();
        for (String symbol : symbols) {
            Coin bySlug = coinService.findBySlug(symbol);
            BinanceTicker inverseQuote = null;
            BinanceTicker binanceTicker = this.getBinanceTicker(symbol + "EUR");
            if (binanceTicker == null) {
                binanceTicker = this.getBinanceTicker("EUR" + symbol);
                inverseQuote = inverseQuote(binanceTicker, "EUR", symbol);
            } else {
                inverseQuote = inverseQuote(binanceTicker, symbol, "EUR");
            }
            if (inverseQuote != null) {
                MarketPriceInfo marketPriceInfo = toMarketPriceInfo(bySlug, inverseQuote);
                if (marketPriceInfo != null) {
                    marketPriceInfoCollection.add(marketPriceInfo);
                }
            }
        }
        return marketPriceInfoCollection;
    }



    public boolean canTrade(String symbol) {
        return isTradableSymbol(symbol);
    }

    public boolean isTradableSymbol(String symbol) {
        return Optional.ofNullable(symbolsCache.get(symbol)).map(s -> s.getStatus().equals("TRADING")).orElse(false);
    }

    private BinanceTicker inverseQuote(BinanceTicker t, String fromCoin, String toCoin) {
        if (fromCoin.equals("EUR")) {
            if (t != null) {
                BinanceTicker binanceTicker = new BinanceTicker();
                binanceTicker.setLastPrice(BigDecimal.ONE.divide(t.getLastPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setAskPrice(BigDecimal.ONE.divide(t.getAskPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setBidPrice(BigDecimal.ONE.divide(t.getBidPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setOpenPrice(BigDecimal.ONE.divide(t.getOpenPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setHighPrice(BigDecimal.ONE.divide(t.getHighPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setLowPrice(BigDecimal.ONE.divide(t.getLowPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setPrevClosePrice(BigDecimal.ONE.divide(t.getPrevClosePrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setWeightedAvgPrice(BigDecimal.ONE.divide(t.getWeightedAvgPrice(), 4, RoundingMode.HALF_UP));
                binanceTicker.setPriceChangePercent(getPriceChangePercentInverse(t));
                binanceTicker.setSymbol(toCoin + "EUR");
                return binanceTicker;
            } else {
                return null;
            }

        }
        return t;
    }


    @NotNull
    private BigDecimal getPriceChangePercentInverse(BinanceTicker binanceTicker) {
        BigDecimal add = binanceTicker.getPriceChangePercent().add(BigDecimal.ONE);
        BigDecimal divide = BigDecimal.ONE.divide(add, 4, RoundingMode.HALF_UP);
        return divide.subtract(BigDecimal.ONE);
    }


    private MarketPriceInfo toMarketPriceInfo(Coin coin, BinanceTicker binanceTicker) {
        if (coin == null) {
            return null;
        }
        MarketPriceInfo marketPriceInfo = new MarketPriceInfo();
        marketPriceInfo.setLastPrice(binanceTicker.getLastPrice());
        marketPriceInfo.setOpenPrice(binanceTicker.getOpenPrice());
        marketPriceInfo.setHighPrice(binanceTicker.getHighPrice());
        marketPriceInfo.setPrevClosePrice(binanceTicker.getPrevClosePrice());
        marketPriceInfo.setSymbol(binanceTicker.getSymbol());
        marketPriceInfo.setBaseSlug(coin.getSlug());
        marketPriceInfo.setPriceChangePercent(binanceTicker.getPriceChangePercent());
        marketPriceInfo.setCoinLogoUrl(coin.getLogoUrl());
        marketPriceInfo.setCoinName(coin.getName());
        return marketPriceInfo;
    }


    public Coin getAvailableCoinForTrading(String fromCoin, String toCoin) {
        BinanceTicker binanceTicker = this.getBinanceTicker(fromCoin + toCoin);
        if (binanceTicker == null) {
            binanceTicker = this.getBinanceTicker(toCoin + fromCoin);
        }
        return binanceTicker == null ? null : coinService.findBySlug(toCoin);
    }

    public Coin getTradingPairs(String fromCoin, String toCoin) {
        BinanceTicker binanceTicker = this.getBinanceTicker(fromCoin + toCoin);
        return binanceTicker == null ? null : coinService.findBySlug(toCoin);
    }


}
