package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vulturi.trading.api.exceptions.ApiException;


public interface ScoreChainRequester {
    ScoreChainScoring getScore(GetScoreRequest scoringRequest) throws JsonProcessingException, ApiException;

}
