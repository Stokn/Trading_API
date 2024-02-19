package com.vulturi.trading.api.services.ohlc;





import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLC;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLCFilter;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLCSource;
import com.vulturi.trading.api.util.SpringCtx;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface OHLCService<T extends OHLC> {

    static OHLCService<? extends OHLC> getServiceForFrequency(Frequency frequency) {
        switch (frequency) {
            case DAY:
                return SpringCtx.getAppCtx().getBean(DailyOHLCService.class);
            case HOUR:
                return SpringCtx.getAppCtx().getBean(HourlyOHLCService.class);
            case MINUTE:
                return SpringCtx.getAppCtx().getBean(MinuteOHLCService.class);
        }
        return null;
    }

    OHLC get(String productCode, LocalDateTime ts, OHLCSource source) throws ApiException;


    void feedLastPrices() throws InterruptedException;

    List<T> getFromCache(String symbol);


    OHLC saveOrUpdate(OHLC ohlc);

    Collection<OHLC> saveAll(Collection<OHLC> ohlcCollection);

    void saveHistoricalDataFrom(OHLCFilter filter) throws InterruptedException;

    Collection<OHLC> filter(OHLCFilter filter) throws ApiException;

    void delete(Frequency frequency, String productCode, LocalDateTime ts, OHLCSource source);
}
