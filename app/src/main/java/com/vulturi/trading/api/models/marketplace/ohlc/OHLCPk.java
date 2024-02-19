package com.vulturi.trading.api.models.marketplace.ohlc;

import jakarta.persistence.Embeddable;
import lombok.*;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

@Embeddable
@Builder
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
public class OHLCPk implements Serializable {

    private String productCode;
    private LocalDateTime ts;
    private OHLCSource source;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OHLCPk ohlcPk = (OHLCPk) o;
        return Objects.equals(productCode, ohlcPk.productCode) && Objects.equals(ts, ohlcPk.ts) && source == ohlcPk.source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(productCode, ts, source);
    }
}
