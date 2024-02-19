package com.vulturi.trading.api.services.ohlc;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.vulturi.trading.api.backend.binance.BinanceOHLCRetriever;
import com.vulturi.trading.api.backend.binance.BinanceSymbol;
import com.vulturi.trading.api.backend.binance.OHLCRetriever;
import com.vulturi.trading.api.dao.ohlc.OHLCDao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.marketplace.ohlc.*;
import com.vulturi.trading.api.services.marketplace.BinanceService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.endpoints.internal.Value;


import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public abstract class AbstractOHLCService<T extends OHLC> implements OHLCService<T> {

    @Autowired
    private OHLCDao<T> dao;


    private List<OHLCRetriever> ohlcRetrievers = List.of(new BinanceOHLCRetriever());


    private Map<String, List<T>> cache = Maps.newConcurrentMap();

    private LocalDateTime minTsInCache;

    @Autowired
    private BinanceService binanceService;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    private void refreshCache() {

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        minTsInCache = getOffSet(now);
        cache = dao.findAllBetweenTs(minTsInCache, now)
                .stream().collect(Collectors.groupingBy(o -> o.getPk().getProductCode()));
        log.info("{} OHLC saved in cache", cache.size());
        log.info("minTsInCache {}", minTsInCache);
    }

    @Override
    public List<T> getFromCache(String symbol) {
        return cache.get(symbol);
    }

    @Scheduled(cron = "@daily")
    @Override
    public void feedLastPrices() throws InterruptedException {
        Set<String> productCodes = cache.keySet();
        for (String code : productCodes) {
            List<T> ohlcList = cache.get(code);
            if (ohlcList != null) {
                T mostRecentQuote = ohlcList.stream().max(Comparator.comparing(t -> t.getPk().getTs())).orElse(null);
                if (mostRecentQuote != null) {
                    LocalDateTime ts = mostRecentQuote.getPk().getTs();
                    OHLCFilter ohlcFilter = OHLCFilter.builder()
                            .minTs(ts)
                            .maxTs(LocalDateTime.now(Clock.systemUTC()))
                            .productCodes(List.of(code))
                            .frequency(getFrequency())
                            .build();
                    saveHistoricalDataFrom(ohlcFilter);
                }
            }
        }
    }

    @Override
    public OHLC get(String productCode, LocalDateTime ts, OHLCSource source) throws ApiException {
        //we persist the data from backend
        if (getSourcesToRetrieve().contains(source)) {
            if (ts.compareTo(minTsInCache) > 0) {
                List<T> ohlcList = cache.get(productCode);
                if (ohlcList != null) {
                    T existingOHLC = ohlcList.stream().filter(
                                    t -> t.getPk().getTs().compareTo(ts) == 0
                            ).findFirst()
                            .orElse(null);
                    if (existingOHLC == null) {
                        return getOhlc(productCode, ts, source);
                    }
                }
                return getOhlc(productCode, ts, source);
            }
            return dao.findById(OHLCPk.builder().productCode(productCode).ts(ts).source(source).build()).orElse(null);
        }
        throw new ApiException(ApiError.MISSING_VALUE, "We are not retrieving source requested");
    }

    private OHLC getOhlc(String productCode, LocalDateTime ts, OHLCSource source) throws ApiException {
        BinanceSymbol symbol = binanceService.getSymbol(productCode);
        if (symbol == null) {
            throw new ApiException(ApiError.NULL_VALUE, String.format("productCode %s does not exist", productCode));
        }
        OHLC ohlc = retrieveFromBackend(source, getFrequency(), symbol, ts);
        return ohlc;
    }


    private OHLC instantiateOHLCForFrequency(Frequency frequency, String productCode, LocalDateTime ts) {
        switch (frequency) {
            case DAY:
                return DailyOHLC.builder().productCode(productCode).ts(ts).build();
            case HOUR:
                return HourlyOHLC.builder().productCode(productCode).ts(ts).build();
            case MINUTE:
                return MinuteOHLC.builder().productCode(productCode).ts(ts).build();
            default:
                return null;
        }
    }


    public Collection<OHLC> saveAll(Collection<OHLC> ohlcCollection) {
        return ohlcCollection.stream().map(this::saveOrUpdate).toList();
    }

    @Override
    public OHLC saveOrUpdate(OHLC ohlc) {
        try {
            T save = dao.save(convertToSubtype(ohlc));
            if (ohlc.getPk().getTs().compareTo(minTsInCache) > 0) {
                cache.computeIfAbsent(ohlc.getPk().getProductCode(), k -> new ArrayList<>());
                cache.get(ohlc.getPk().getProductCode()).add(convertToSubtype(ohlc));
            }
            return save;
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }


    @Override
    public Collection<OHLC> filter(OHLCFilter filter) throws ApiException {
        List<OHLC> data = new ArrayList<>();
        if (filter.getMinTs() == null) {
            throw new ApiException(ApiError.MISSING_VALUE);
        }
        if (filter.getMinTs().compareTo(minTsInCache) > 0) {
            data.addAll(cache.values().stream().flatMap(Collection::stream).filter(filter.toPredicate()).toList());
            if (data.size() != 0) {
                return data;
            }
            throw new ApiException(ApiError.NULL_VALUE, String.format("No market fata available for this filter %s please contact : dsi@bitscor.com", filter));
        } else {
            throw new ApiException(ApiError.NULL_VALUE, "Please contact dsi@bitscor.com if you want to access data before 2years");
        }


    }

    public void saveHistoricalDataFrom(OHLCFilter filter) throws InterruptedException {
        OHLCService<? extends OHLC> serviceForFrequency = OHLCService.getServiceForFrequency(filter.getFrequency());
        for (String symbol : filter.getProductCodes()) {
            retrieveMultipleFromOHLC(OHLCSource.BINANCE, filter.getFrequency(), binanceService.getSymbol(symbol), filter.getMinTs());
            log.info("Historical quotes saved for {} from {}", symbol, filter.getMinTs());
        }
        refreshCache();
    }

    @Override
    public void delete(Frequency frequency, String productCode, LocalDateTime ts, OHLCSource source) {
        dao.deleteById(OHLCPk.builder().productCode(productCode).ts(ts).source(source).build());
    }

    //@Scheduled(cron = "50 0/1 * * * *")
    public void scheduleOHLCRetrieval() throws InterruptedException {
        log.info("SCHEDULED OHLC RETRIEVAL");
        Collection<String> symbols = cache.keySet();
        for (Frequency frequency : Frequency.values()) {
            log.info("Checking for frequency {}", frequency);
            LocalDateTime lastOhlcTsForFrequency = getLastOhlcTsForFrequency(frequency);
            if (lastOhlcTsForFrequency != null) {
                for (String symbol : symbols) {
                    for (OHLCSource source : getSourcesToRetrieve()) {
                        LocalDateTime tsToRequest = lastOhlcTsForFrequency;
                        int lookbackCounter = 1;
                        while (lookbackCounter <= 5) {
                            try {
                                lookbackCounter++;
                                OHLC ohlc = OHLCService.getServiceForFrequency(frequency).get(symbol, tsToRequest, source);
                                if (ohlc == null) {
                                    BinanceSymbol binanceSymbol = binanceService.getSymbol(symbol);
                                    retrieveFromBackend(source, frequency, binanceSymbol, tsToRequest);
                                    Thread.sleep(100);
                                }
                            } catch (Exception e) {
                                log.error("Error getting ohlc", e);
                            }
                            tsToRequest = tsToRequest.minus(1, frequency == Frequency.DAY ? ChronoUnit.DAYS : frequency == Frequency.HOUR ? ChronoUnit.HOURS : ChronoUnit.MINUTES);
                        }
                    }

                }
            }else {
                feedLastPrices();
            }

        }
    }

    protected abstract Frequency getFrequency();

    protected abstract LocalDateTime getOffSet(LocalDateTime now);


    private LocalDateTime getLastOhlcTsForFrequency(Frequency frequency) {
        switch (frequency) {
            case DAY:
                return LocalDateTime.now(Clock.systemUTC()).minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case HOUR:
                return LocalDateTime.now(Clock.systemUTC()).minusHours(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            case MINUTE:
                return LocalDateTime.now(Clock.systemUTC()).minusMinutes(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
            default:
                return null;
        }
    }

    private Collection<OHLCSource> getSourcesToRetrieve() {
        return Lists.newArrayList(OHLCSource.BINANCE);
    }

    private OHLC retrieveFromBackend(OHLCSource source, Frequency frequency, BinanceSymbol symbol, LocalDateTime ts) {
        OHLCRetriever retriever = getRetrieverForSource(source);
        OHLC ohlc = retriever.retrieveFromBackend(frequency, symbol, ts);
        return saveOrUpdate(ohlc);
    }

    public void retrieveMultipleFromOHLC(OHLCSource ohlcSource, Frequency frequency, BinanceSymbol symbol, LocalDateTime minTs) throws InterruptedException {
        OHLCRetriever retrieverForSource = getRetrieverForSource(ohlcSource);
        Collection<OHLC> ohlcCollection = retrieverForSource.retrieveHistoricalFromBackend(frequency, symbol, minTs);
        saveAll(ohlcCollection);
    }

    private OHLCRetriever getRetrieverForSource(OHLCSource source) {
        return ohlcRetrievers.stream().filter(r -> r.getSource() == source).findAny().orElse(null);
    }

    private <T> T convertToSubtype(OHLC ohlc) {
        try {
            return (T) ohlc;
        } catch (ClassCastException e) {
            throw new RuntimeException("Conversion failed for OHLC", e);
        }
    }

}
