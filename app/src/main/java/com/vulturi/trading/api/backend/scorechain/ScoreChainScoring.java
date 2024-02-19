package com.vulturi.trading.api.backend.scorechain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ScoreChainScoring {
    private BigDecimal score;
    private RiskSeverity severity;

}
