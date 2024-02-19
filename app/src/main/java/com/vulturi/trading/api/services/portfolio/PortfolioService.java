package com.vulturi.trading.api.services.portfolio;

import com.vulturi.trading.api.models.portfolio.Portfolio;

import java.util.Collection;

public interface PortfolioService {
    Portfolio save(Portfolio portfolio );
    Collection<Portfolio> getByAccountId(String accountId);
    Portfolio getByAccountIdAndPortfolioId(String accountId,String portfolioId);

}
