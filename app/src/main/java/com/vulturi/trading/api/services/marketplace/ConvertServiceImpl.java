package com.vulturi.trading.api.services.marketplace;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.services.coin.CoinService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ConvertServiceImpl implements ConvertService {
    @Autowired
    private CoinService coinService;
    @Autowired
    private BinanceService binanceService;

    private Map<String, List<Coin>> cache = Maps.newConcurrentMap();

    private Map<String,Boolean> tradingPairsCache = Maps.newConcurrentMap();

    private Map<String, ArrayList<String>> getCombination(List<String> coins) {
        Map<String, ArrayList<String>> combinations = coins.stream().collect(Collectors.toMap(s -> s, s -> new ArrayList<String>()));
        for (String fromCoin : coins) {
            for (String toCoin : coins) {
                if (fromCoin != null) {
                    combinations.get(fromCoin).add(toCoin);
                }
            }
        }
        log.info("All the combination by coin {}", combinations);
        return combinations;
    }

    @PostConstruct
    void init() {
        refreshCache();
    }


    private void refreshCache() {
        Collection<Coin> coins = new ArrayList<>();
        cache.clear();
        List<String> allCoins = coinService.findAll().stream().map(Coin::getSlug).toList();
        Map<String, ArrayList<String>> combination = getCombination(allCoins);
        for (Map.Entry<String, ArrayList<String>> entry : combination.entrySet()) {
            String fromCoin = entry.getKey();
            ArrayList<String> tgtToCoins = entry.getValue();
            cache.put(fromCoin, new ArrayList<Coin>());
            for (String tgtCoin : tgtToCoins) {
                Coin availableCoinForTrading = binanceService.getAvailableCoinForTrading(fromCoin, tgtCoin);
                Coin availablePairsForTrading = binanceService.getTradingPairs(fromCoin, tgtCoin);
                if (availableCoinForTrading != null) {
                    cache.get(fromCoin).add(availableCoinForTrading);
                }
                if(availablePairsForTrading!=null){
                    tradingPairsCache.put(fromCoin+availablePairsForTrading.getSlug(),true);
                }
            }
        }
    }

    @Override
    public Collection<Coin> getAvailableCoinForTrading(String fromCoin) {
        return cache.get(fromCoin);
    }

    @Override
    public Collection<String> getAvailablePairsForTrading() {
        return tradingPairsCache.keySet();
    }

    @Override
    public void getTradingPairDetails(String pair) {
        binanceService.getBinanceTicker(pair);
    }
}
