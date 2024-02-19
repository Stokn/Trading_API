package com.vulturi.trading.api.models.marketplace.ohlc;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "T_DAILY_OHLC")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DailyOHLC extends OHLC {

    @Builder
    public DailyOHLC(String productCode, LocalDateTime ts, OHLCSource source, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume) {
        super(OHLCPk.builder().productCode(productCode).ts(ts).source(source).build(), open, high, low, close, volume);
    }

    @Override
    public Frequency getFrequency() {
        return Frequency.DAY;
    }
}
