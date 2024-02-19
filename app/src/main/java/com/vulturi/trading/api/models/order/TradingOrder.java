package com.vulturi.trading.api.models.order;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;
@Entity
@Table(name = "T_TRADING_ORDERS")
@JsonIgnoreProperties(ignoreUnknown = true)
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter @Setter
public  class TradingOrder {
    private String accountId;
    private String portfolioId;
    @Id
    private String orderId= UUID.randomUUID().toString();
    private String fromAsset;
    private String toAsset;
    private BigDecimal quantity;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
    private ExchangePlatform exchange;
    private boolean schedule;

}
