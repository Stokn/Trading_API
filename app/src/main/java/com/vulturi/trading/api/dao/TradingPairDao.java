package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.exchange.TradingPair;
import com.vulturi.trading.api.models.exchange.TradingPairPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingPairDao extends JpaRepository<TradingPair, TradingPairPk> {
}
