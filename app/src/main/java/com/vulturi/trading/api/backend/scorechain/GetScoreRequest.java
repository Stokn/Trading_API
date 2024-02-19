package com.vulturi.trading.api.backend.scorechain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GetScoreRequest {
    private String objectId;
    private ScoreChainObjType objectType;
    private ScoreChainAnalysisType analysisType;
    private Blockchain blockchain;
    private ScoreChainCoin coin;

    @Override
    public String toString() {
        return "ScoringRequest{" +
                "objectId='" + objectId + '\'' +
                ", objectType=" + objectType +
                ", analysisType=" + analysisType +
                ", blockchain=" + blockchain +
                ", coin=" + coin +
                '}';
    }
}
