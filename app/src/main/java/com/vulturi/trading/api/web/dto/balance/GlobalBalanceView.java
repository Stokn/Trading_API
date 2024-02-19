package com.vulturi.trading.api.web.dto.balance;

import com.vulturi.trading.api.models.balance.PortfolioBalance;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

@Data
public class GlobalBalanceView {
    private String accountId;
    private LocalDateTime ts;
    private Collection<PortfolioBalanceView> portfolioBalances;
}
