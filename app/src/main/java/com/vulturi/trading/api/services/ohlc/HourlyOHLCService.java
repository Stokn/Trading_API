package com.vulturi.trading.api.services.ohlc;


import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import com.vulturi.trading.api.models.marketplace.ohlc.HourlyOHLC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class HourlyOHLCService extends AbstractOHLCService<HourlyOHLC> {


    @Override
    protected LocalDateTime getOffSet(LocalDateTime now) {
        return now.minusDays(365);
    }

    @Override
    protected Frequency getFrequency() {
        return Frequency.HOUR;
    }
}
