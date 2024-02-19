package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.exchange.TradingTransactionPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingTransactionDao extends JpaRepository<TradingTransaction, TradingTransactionPk> {
}
