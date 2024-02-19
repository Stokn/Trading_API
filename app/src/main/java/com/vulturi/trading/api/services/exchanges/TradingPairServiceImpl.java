package com.vulturi.trading.api.services.exchanges;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.TradingPairDao;
import com.vulturi.trading.api.models.exchange.TradingPair;
import com.vulturi.trading.api.models.exchange.TradingPairPk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TradingPairServiceImpl implements TradingPairService {

    @Autowired
    private TradingPairDao dao;

    private Map<TradingPairPk, TradingPair> cache = Maps.newConcurrentMap();

    private void refreshCache(){
        cache  =dao.findAll().stream().collect(Collectors.toMap(TradingPair::getPk, t->t));


    }
    @Override
    public List<TradingPair> get(String pair) {
        return cache.values().stream().filter(tradingPair -> tradingPair.getPk().getPair().equals(pair)).toList();
    }

    @Override
    public TradingPair filter() {
        return null;
    }

    @Override
    public TradingPair saveOrUpdate(TradingPair tradingPair) {
        TradingPair savedTradingPair = dao.save(tradingPair);
        cache.put(savedTradingPair.getPk(),savedTradingPair);
        return savedTradingPair;
    }
}
