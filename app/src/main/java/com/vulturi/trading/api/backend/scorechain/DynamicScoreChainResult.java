package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Collection;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DynamicScoreChainResult {
    private BigDecimal score;
    private RiskSeverity severity;
    private Collection<ScoreChainDetails> details;
}
