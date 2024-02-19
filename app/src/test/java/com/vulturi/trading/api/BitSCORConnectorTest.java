package com.vulturi.trading.api;
import com.vulturi.trading.api.backend.bitscor.ESGScore;
import com.vulturi.trading.api.backend.bitscor.GetPortfolioAllocationRating;
import com.vulturi.trading.api.services.esg.ESGDataService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
@ActiveProfiles("test")
public class BitSCORConnectorTest {
    @Autowired
    private ESGDataService esgDataService;


    @Test
    @Disabled
    public void testGetLatestQuotes() throws IOException {
        GetPortfolioAllocationRating getPortfolioAllocationRating = new GetPortfolioAllocationRating();
        getPortfolioAllocationRating.setProductCode("MAIN_PORTFOLIO");
        getPortfolioAllocationRating.setAllocation(Map.of("BTC", BigDecimal.valueOf(0.2),"USDT",BigDecimal.valueOf(300),"ETH",BigDecimal.valueOf(2.5)));
        ESGScore lastEsgScores = esgDataService.getPortfolioRating(getPortfolioAllocationRating);


    }







}
