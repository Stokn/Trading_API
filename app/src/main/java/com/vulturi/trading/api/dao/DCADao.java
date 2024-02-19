package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.dca.DCA;
import com.vulturi.trading.api.models.dca.DCAFrequency;
import com.vulturi.trading.api.models.dca.DCAPk;
import com.vulturi.trading.api.models.dca.DCAStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DCADao extends JpaRepository<DCA,DCAPk> {


    @Query("select dca from DCA dca where dca.status = :status")
    List<DCA> findAllByStatus(@Param("status") DCAStatus status);

    @Query("select dca from DCA dca where dca.pk.accountId = :accountId")
    List<DCA> findByAccountId(@Param("accountId") String accountId);

    @Query("select dca from DCA dca where dca.status = :status and dca.frequency= :frequency")
    List<DCA> findAllByStatusAndFrequency(@Param("status") DCAStatus status, @Param("frequency") DCAFrequency frequency);

}
