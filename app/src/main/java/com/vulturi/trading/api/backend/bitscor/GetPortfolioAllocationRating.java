package com.vulturi.trading.api.backend.bitscor;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Map;

@Getter
@Setter
@ToString
public class GetPortfolioAllocationRating {
    private String productCode;
    private Map<String, BigDecimal> allocation;
}