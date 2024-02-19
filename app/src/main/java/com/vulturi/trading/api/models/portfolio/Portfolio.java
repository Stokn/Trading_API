package com.vulturi.trading.api.models.portfolio;

import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "T_PORTFOLIO")
public class Portfolio {
    @Id
    private String portfolioId = UUID.randomUUID().toString();
    private String name;
    private String accountId;
    private LocalDateTime creationTs=LocalDateTime.now(Clock.systemUTC());
    private boolean publicStrategy = false;
    private PortfolioAccountType portfolioAccountType;
}
