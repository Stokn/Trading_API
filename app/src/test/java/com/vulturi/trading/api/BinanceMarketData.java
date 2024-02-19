package com.vulturi.trading.api;

import com.vulturi.trading.api.backend.binance.BinanceSymbol;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLCSource;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;
import com.vulturi.trading.api.services.marketplace.BinanceService;
import com.vulturi.trading.api.services.ohlc.HourlyOHLCService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

@SpringBootTest
@ActiveProfiles("test")
public class BinanceMarketData {

    @Autowired
    private HourlyOHLCService hourlyOHLCService;

    @Autowired
    private BinanceService binanceService;


    @Test
    public void get() throws ApiException, InterruptedException {

        BinanceSymbol btcusdt = binanceService.getSymbol("BTCUSDT");

        hourlyOHLCService.retrieveMultipleFromOHLC(OHLCSource.BINANCE, Frequency.HOUR,btcusdt, LocalDateTime.now(Clock.systemUTC()).minusDays(360));



        //GlobalBalance globalBalance = globalBalanceService.get(account, LocalDateTime.now());
        //GlobalBalanceView globalBalanceView = globalBalanceFactory.toGlobalBalanceView(globalBalance);
    }

}
