package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.marketplace.TradingPairInfo;
import com.vulturi.trading.api.models.marketplace.ohlc.MarketDataClosePrice;
import com.vulturi.trading.api.services.marketplace.BinanceService;
import com.vulturi.trading.api.services.marketplace.MarketPlaceService;
import com.vulturi.trading.api.services.ohlc.HourlyOHLCService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/v1/market-place")
public class MarketPlaceController {


    @Autowired
    private MarketPlaceService marketPlaceService;

    @Autowired
    private HourlyOHLCService hourlyOHLCService;

    @Autowired
    private BinanceService binanceService;



    @CrossOrigin(origins = {"*"})
    @GetMapping("/market-data/last")
    public ResponseEntity<ApiResponse<Collection<TradingPairInfo>>>get() throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(marketPlaceService.getListedPairs()));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/market-data/{symbol}")
    public ResponseEntity<ApiResponse<MarketDataClosePrice>> getForCoin(@PathVariable("symbol") String symbol) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(marketPlaceService.getOHLC(symbol)));
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping("/market-data/feed/{symbol}")
    public ResponseEntity<ApiResponse<Boolean>>feed(@PathVariable("symbol")String symbol) throws ApiException, InterruptedException {
        marketPlaceService.saveHistorical(symbol);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(true));
    }



}
