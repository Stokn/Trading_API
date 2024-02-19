package com.vulturi.trading.api.services.esg;

import com.vulturi.trading.api.backend.bitscor.ESGScore;
import com.vulturi.trading.api.backend.bitscor.GetPortfolioAllocationRating;

import java.io.IOException;
import java.util.Collection;

public interface ESGDataService {
    Collection<ESGScore> getAll();

    ESGScore getPortfolioRating(GetPortfolioAllocationRating getPortfolioAllocationRating) throws IOException;

    ESGScore get(String coin);
}
