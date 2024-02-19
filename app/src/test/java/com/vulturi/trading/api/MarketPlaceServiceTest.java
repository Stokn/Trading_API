package com.vulturi.trading.api;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.services.marketplace.MarketPlaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class MarketPlaceServiceTest {
    @Autowired
    private MarketPlaceService marketPlaceService;

    @Test
    public void get() throws ApiException {
    }
}
