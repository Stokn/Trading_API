package com.vulturi.trading.api.web.dto.balance;

import com.vulturi.trading.api.models.balance.AssetBalance;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Data
public class PortfolioBalanceView {
    private String accountId;
    private String portfolioId;
    private PortfolioAccountType portfolioAccountType;
    private LocalDateTime ts;
    private Map<LocalDateTime, BigDecimal> historicalBalancePrices;
    private Collection<AssetBalanceView> assetBalances;
}
