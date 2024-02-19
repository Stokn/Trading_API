package com.vulturi.trading.api.dao.ohlc;


import com.vulturi.trading.api.models.marketplace.ohlc.SecondOHLC;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SecondOHLCDao extends OHLCDao<SecondOHLC> {

    @Query("from SecondOHLC secondOHLC where secondOHLC.pk.ts BETWEEN :minTs AND :maxTs")
    List<SecondOHLC> findAllBetweenTs(
            @Param("minTs") LocalDateTime minTs, @Param("maxTs") LocalDateTime maxTs);
}
