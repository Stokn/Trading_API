package com.vulturi.trading.api.services.kyt;

import com.vulturi.trading.api.backend.scorechain.GetScoreRequest;

public interface KytService {
    void analyseTransaction(GetScoreRequest getScoreRequest);

    void scoreAddress(GetScoreRequest getScoreRequest);


}
