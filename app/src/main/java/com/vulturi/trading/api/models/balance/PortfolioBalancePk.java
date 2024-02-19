package com.vulturi.trading.api.models.balance;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.io.Serializable;
import java.time.LocalDateTime;

@Getter@Setter@ToString@Embeddable
public class PortfolioBalancePk implements Serializable {
    @Column
    private String accountId;
    @Column
    private String portfolioId;
    @Column
    private LocalDateTime ts;
}
