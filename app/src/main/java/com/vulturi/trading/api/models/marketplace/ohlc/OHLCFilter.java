package com.vulturi.trading.api.models.marketplace.ohlc;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Predicate;

@Builder@Setter@Getter@ToString
public class OHLCFilter {

    private LocalDateTime maxTs;
    private LocalDateTime minTs;
    private Collection<String> productCodes;
    private Frequency frequency;

    public OHLCFilter(LocalDateTime maxTs, LocalDateTime minTs, Collection<String> productCodes, Frequency frequency) {
        this.maxTs = maxTs;
        this.minTs = minTs;
        this.productCodes = productCodes;
        this.frequency = frequency;
    }



    private Predicate<OHLC> getMinTsPredicate() {
        return this.minTs != null ? (s) -> {
            return s.getPk().getTs().compareTo(minTs) >= 0;
        } : (p) -> {
            return true;
        };
    }


    private Predicate<OHLC> getMaxTsPredicate() {
        return this.maxTs != null ? (s) -> {
            return s.getPk().getTs().compareTo(maxTs) <= 0;
        } : (p) -> {
            return true;
        };
    }

    private Predicate<OHLC> getProductCode() {
        return this.productCodes != null ? (s) -> {
            return this.productCodes.contains(s.getPk().getProductCode());
        } : (p) -> {
            return true;
        };
    }

    public Predicate<OHLC> toPredicate() {
        return this.getMaxTsPredicate().and(this.getMinTsPredicate()).and(this.getProductCode());
    }
}
