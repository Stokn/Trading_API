package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.portfolio.Portfolio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortfolioDao extends JpaRepository<Portfolio,String> {
}
