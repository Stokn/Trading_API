package com.vulturi.trading.api.models.kyt;

import com.vulturi.trading.api.backend.scorechain.ScoreChainScoring;
import lombok.Data;

@Data
public class KytAnalysis {
    private ScoreChainScoring scoreChainScoring;
}
