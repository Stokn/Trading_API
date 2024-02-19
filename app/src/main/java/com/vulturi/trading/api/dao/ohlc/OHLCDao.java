package com.vulturi.trading.api.dao.ohlc;


import com.vulturi.trading.api.models.marketplace.ohlc.OHLC;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLCPk;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

@NoRepositoryBean
public interface OHLCDao<T extends OHLC> extends JpaRepository<T, OHLCPk> {

    List<T> findAllBetweenTs(
            @Param("minTs") LocalDateTime minTs, @Param("maxTs") LocalDateTime maxTs);

}
