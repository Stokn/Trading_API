package com.vulturi.trading.api.models.marketplace.ohlc;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
@Data
public class OHLCTimeFrame {
    private Map<LocalDateTime, BigDecimal> dailyPrices;
    private Map<LocalDateTime, BigDecimal> weeklyPrices;
    private Map<LocalDateTime, BigDecimal> monthlyPrices;
    private Map<LocalDateTime, BigDecimal> annualPrices;
}
