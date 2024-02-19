package com.vulturi.trading.api.backend.marketdata;


import com.vulturi.trading.api.models.predicates.OHLCFilter;
import com.vulturi.trading.api.models.predicates.TickerFilter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@FeignClient("market-data")
public interface FeignMarketDataService {

}
