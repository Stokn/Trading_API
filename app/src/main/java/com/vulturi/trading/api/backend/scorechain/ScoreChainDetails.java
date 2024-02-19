package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreChainDetails {
    private String referenceAddress;
    private BigDecimal amount;
    private BigDecimal amountUsd;
    private BigDecimal percentage;
    private String name;
    private String type;
    private BigDecimal score;
    private RiskSeverity severity;

}
