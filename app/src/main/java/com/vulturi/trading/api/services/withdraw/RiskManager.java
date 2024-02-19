package com.vulturi.trading.api.services.withdraw;

import com.vulturi.trading.api.backend.scorechain.ScoreChainScoring;

import java.math.BigDecimal;

public class RiskManager {
    public static boolean canWithdraw(ScoreChainScoring riskScore) {
        // todo define rule

        if (canWithdraw(riskScore.getScore())) {
            return true;
        }
        return false;
    }

    public static boolean canWithdraw(BigDecimal riskScore) {
        // todo define rule

        if (riskScore.compareTo(BigDecimal.valueOf(0.5)) >= 0) {
            return true;
        }
        return false;
    }
}
