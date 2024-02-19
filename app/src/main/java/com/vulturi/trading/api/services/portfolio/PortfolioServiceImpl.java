package com.vulturi.trading.api.services.portfolio;

import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
@Service@Slf4j
public class PortfolioServiceImpl implements PortfolioService {

    @Autowired
    private UserService userService;

    @Override
    public Portfolio save(Portfolio portfolio) {
        return null;
    }

    @Override
    public Collection<Portfolio> getByAccountId(String accountId) {
        return null;
    }


    @Override
    public Portfolio getByAccountIdAndPortfolioId(String accountId, String portfolioId) {
        return null;
    }
}
