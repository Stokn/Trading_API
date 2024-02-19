package com.vulturi.trading.api.models.exchange;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

@Getter@Setter@ToString@Embeddable
public class TradingTransactionPk implements Serializable {
    private String accountId;
    private String portfolioId;
    private LocalDateTime ts = LocalDateTime.now(Clock.systemUTC());
}
