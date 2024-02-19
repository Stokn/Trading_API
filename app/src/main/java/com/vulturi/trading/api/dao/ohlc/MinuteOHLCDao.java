package com.vulturi.trading.api.dao.ohlc;



import com.vulturi.trading.api.models.marketplace.ohlc.MinuteOHLC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MinuteOHLCDao extends OHLCDao<MinuteOHLC> {

    @Query("from MinuteOHLC minuteOHLC where minuteOHLC.pk.ts BETWEEN :minTs AND :maxTs")
    List<MinuteOHLC> findAllBetweenTs(
            @Param("minTs") LocalDateTime minTs, @Param("maxTs") LocalDateTime maxTs);
}
