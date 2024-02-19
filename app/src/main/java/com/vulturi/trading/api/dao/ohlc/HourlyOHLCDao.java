package com.vulturi.trading.api.dao.ohlc;



import com.vulturi.trading.api.models.marketplace.ohlc.HourlyOHLC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface HourlyOHLCDao extends OHLCDao<HourlyOHLC> {

    @Query("from HourlyOHLC hourlyOHLC where hourlyOHLC.pk.ts BETWEEN :minTs AND :maxTs")
    List<HourlyOHLC> findAllBetweenTs(
            @Param("minTs") LocalDateTime minTs, @Param("maxTs") LocalDateTime maxTs);
}
