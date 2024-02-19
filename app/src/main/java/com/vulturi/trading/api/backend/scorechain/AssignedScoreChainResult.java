package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter@Setter@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignedScoreChainResult {
    private BigDecimal score;
    private RiskSeverity severity;
    private AssignedScoreChainDetails details;
}
