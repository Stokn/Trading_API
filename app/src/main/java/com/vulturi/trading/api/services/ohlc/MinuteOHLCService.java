package com.vulturi.trading.api.services.ohlc;


import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import com.vulturi.trading.api.models.marketplace.ohlc.MinuteOHLC;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class MinuteOHLCService extends AbstractOHLCService<MinuteOHLC> {


    @Override
    protected LocalDateTime getOffSet(LocalDateTime now) {
        return now.minusMinutes(5);
    }
    @Override
    protected Frequency getFrequency() {
        return Frequency.MINUTE;
    }
}
