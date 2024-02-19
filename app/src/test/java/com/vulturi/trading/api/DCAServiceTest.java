package com.vulturi.trading.api;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.dca.DCA;
import com.vulturi.trading.api.models.dca.DCAFrequency;
import com.vulturi.trading.api.models.dca.DCAStatus;
import com.vulturi.trading.api.services.dca.DCAService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

@SpringBootTest
@ActiveProfiles("test")
public class DCAServiceTest {

    @Autowired
    private DCAService dcaService;

    @Test
    public void get() throws ApiException {
        DCA dca = new DCA();
        dca.getPk().setPortfolioId(UUID.randomUUID().toString());
        dca.getPk().setAccountId("account-test");
        dca.setFromCoin("USDT");
        dca.setToCoin("BTC");
        dca.setFrequency(DCAFrequency.DAILY);
        dca.setStatus(DCAStatus.ACTIVE);
        DCA savedDCA = dcaService.save(dca);

    }

}
