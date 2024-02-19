package com.vulturi.trading.api.models.marketplace.ohlc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.MappedSuperclass;
import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@MappedSuperclass
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "frequency", visible = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@Setter
public abstract class OHLC {

    @EmbeddedId
    @EqualsAndHashCode.Include
    private OHLCPk pk;

    @Column(precision = 27, scale = 10)
    private BigDecimal open;
    @Column(precision = 27, scale = 10)
    private BigDecimal high;
    @Column(precision = 27, scale = 10)
    private BigDecimal low;
    @Column(precision = 27, scale = 10)
    private BigDecimal close;
    @Column(precision = 27, scale = 10)
    private BigDecimal volume;

    public abstract Frequency getFrequency();

    public static OHLC build(Frequency frequency, String productCode, LocalDateTime ts, OHLCSource source, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume) {
        switch (frequency) {
            case DAY:
                return new DailyOHLC(productCode, ts, source, open, high, low, close, volume);
            case HOUR:
                return new HourlyOHLC(productCode, ts, source, open, high, low, close, volume);
            case MINUTE:
                return new MinuteOHLC(productCode, ts, source, open, high, low, close, volume);
            case SECOND:
                return new SecondOHLC(productCode, ts, source, open, high, low, close, volume);
            default:
                return null;
        }
    }
}
