package com.vulturi.trading.api.models.portfolio;

import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import lombok.Data;

@Data
public class PortfolioCreationRequest {
    private String accountId;
    private String name;
    private String portfolioId;
    private PortfolioAccountType portfolioAccountType;
}
