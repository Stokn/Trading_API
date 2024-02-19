package com.vulturi.trading.api.models.exchange;

import com.vulturi.trading.api.models.enums.ExchangePlatform;
import jakarta.persistence.Embeddable;
import lombok.*;


import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TradingPairPk implements Serializable {
    private ExchangePlatform exchange;
    private String pair;
    private String base;
    private String quote;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TradingPairPk that = (TradingPairPk) o;
        return exchange == that.exchange && Objects.equals(pair, that.pair) && Objects.equals(base, that.base) && Objects.equals(quote, that.quote);
    }

    @Override
    public int hashCode() {
        return Objects.hash(exchange, pair, base, quote);
    }





}
