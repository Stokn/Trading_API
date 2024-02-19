package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.coin.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinDao extends JpaRepository<Coin,String> {
}
