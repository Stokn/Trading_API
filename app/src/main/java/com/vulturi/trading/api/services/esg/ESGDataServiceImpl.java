package com.vulturi.trading.api.services.esg;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.backend.bitscor.BitSCORConnector;
import com.vulturi.trading.api.backend.bitscor.ESGScore;
import com.vulturi.trading.api.backend.bitscor.GetPortfolioAllocationRating;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ESGDataServiceImpl implements ESGDataService {

    @Autowired
    private BitSCORConnector bitSCORConnector;

    private Map<String, ESGScore> cache = Maps.newConcurrentMap();

    @PostConstruct
    public void init() throws IOException {
        refreshCache();
    }


    @Scheduled(cron = "@daily")
    private void refreshCache() throws IOException {
        cache = bitSCORConnector.getLastEsgScores().stream().collect(Collectors.toMap(ESGScore::getSymbol, e -> e));
        log.info("{} esg data have been saved in cache", cache.size());
    }

    @Override
    public Collection<ESGScore> getAll() {
        return cache.values();
    }

    @Override
    public ESGScore getPortfolioRating(GetPortfolioAllocationRating getPortfolioAllocationRating) throws IOException {
        return bitSCORConnector.getPortfolioESGRating(getPortfolioAllocationRating);
    }

    @Override
    public ESGScore get(String coin) {
        return cache.get(coin);
    }
}
