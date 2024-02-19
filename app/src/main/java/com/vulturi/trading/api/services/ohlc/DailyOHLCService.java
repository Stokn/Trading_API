package com.vulturi.trading.api.services.ohlc;



import com.vulturi.trading.api.models.marketplace.ohlc.DailyOHLC;
import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DailyOHLCService extends AbstractOHLCService<DailyOHLC> {

    @Override
    protected Frequency getFrequency() {
        return Frequency.DAY;
    }

    @Override
    protected LocalDateTime getOffSet(LocalDateTime now) {
        return now.minusMonths(1);
    }


}
