package com.vulturi.trading.api.models.exchange;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.time.Clock;
import java.time.LocalDateTime;


@Entity
@Table(name = "T_TRADING_PAIR")
@Getter
@Setter
@ToString
public class TradingPair {
    @EmbeddedId
    private TradingPairPk pk = new TradingPairPk();
    private String productCode;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
    private LocalDateTime updatedTs = LocalDateTime.now(Clock.systemUTC());
}
