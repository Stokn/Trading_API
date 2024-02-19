package com.vulturi.trading.api.models.withdraw;

import com.vulturi.trading.api.backend.scorechain.RiskSeverity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "T_WITHDRAW_ADDRESS")
public class WithdrawAddress {
    @Id
    private String address;
    private String accountId;
    private String network;
    private BigDecimal riskScore;
    private RiskSeverity riskSeverity;
    private LocalDateTime creationTs = LocalDateTime.now(Clock.systemUTC());
}
