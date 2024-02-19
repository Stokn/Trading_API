package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.order.TradingOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TradingOrderDao extends JpaRepository<TradingOrder,String> {
}
