package com.vulturi.trading.api.dao.ohlc;



import com.vulturi.trading.api.models.marketplace.ohlc.DailyOHLC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DailyOHLCDao extends OHLCDao<DailyOHLC> {

    @Query("from DailyOHLC dailyOHLC where dailyOHLC.pk.ts BETWEEN :minTs AND :maxTs")
    List<DailyOHLC> findAllBetweenTs(
            @Param("minTs") LocalDateTime minTs, @Param("maxTs") LocalDateTime maxTs);

}
