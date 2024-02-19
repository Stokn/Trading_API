package com.vulturi.trading.api.services.coin;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.coin.CoinUpdateNetwork;
import com.vulturi.trading.api.models.coin.GetCoinCreationRequest;

import java.util.Collection;

public interface CoinService {

    Coin findBySlug (String slug);
    Collection<Coin> findAll();

    Coin create(GetCoinCreationRequest getCoinCreationRequest) throws ApiException;

    Coin update(Coin coin) throws ApiException;

    Coin save(Coin coin);

    Coin updateNetwork(CoinUpdateNetwork coinUpdateNetwork) throws ApiException;

}
