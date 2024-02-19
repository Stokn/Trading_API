package com.vulturi.trading.api.services.coin;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.CoinDao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.coin.CoinUpdateNetwork;
import com.vulturi.trading.api.models.coin.GetCoinCreationRequest;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinDao coinDao;

    private Map<String, Coin> cache = Maps.newConcurrentMap();

    @PostConstruct
    void init() {
        cache = coinDao.findAll().stream().collect(Collectors.toMap(Coin::getSlug, c -> c));
        log.info("{} coins have been saved in cache", cache.size());
    }

    @Override
    public Coin findBySlug(String slug) {
        return cache.get(slug);
    }


    @Override
    public Collection<Coin> findAll() {
        log.info("Requesting all coins");
        return cache.values();
    }

    @Override
    public Coin create(GetCoinCreationRequest getCoinCreationRequest) throws ApiException {
        if(cache.get(getCoinCreationRequest.getSlug())!=null){
            throw new ApiException(ApiError.COIN_ALREADY_EXIST);
        }
        return save(toCoin(getCoinCreationRequest));
    }

    @Override
    public Coin update(Coin coin) throws ApiException {
        Coin existingCoin = cache.get(coin.getSlug());
        if(existingCoin==null){
            throw new ApiException(ApiError.COIN_DOES_NOT_EXIST);
        }
        existingCoin.setName(coin.getName());
        existingCoin.setLogoUrl(coin.getLogoUrl());
        existingCoin.setTypeDeposit(coin.getTypeDeposit());
        existingCoin.setAvailableNetworks(coin.getAvailableNetworks());
        return save(existingCoin);
    }

    private Coin toCoin(GetCoinCreationRequest request){
        Coin coin = new Coin();
        coin.setSlug(request.getSlug());
        coin.setName(request.getName());
        coin.setLogoUrl(request.getLogoUrl());
        coin.setTypeDeposit(request.getTypeDeposit());
        coin.setAvailableNetworks(request.getAvailableNetworks());
        return coin;
    }

    @Override
    public Coin save(Coin coin) {
        Coin savedCoin = coinDao.save(coin);
        cache.put(savedCoin.getSlug(), savedCoin);
        return savedCoin;
    }

    @Override
    public Coin updateNetwork(CoinUpdateNetwork coinUpdateNetwork) throws ApiException {
        Coin existingCoin = cache.get(coinUpdateNetwork.getSlug());
        if (existingCoin == null) {
            throw new ApiException(ApiError.NULL_VALUE, "Not existing coin : " + coinUpdateNetwork.getSlug());
        }
        existingCoin.setAvailableNetworks(coinUpdateNetwork.getNetwork());
        return save(existingCoin);
    }

}
