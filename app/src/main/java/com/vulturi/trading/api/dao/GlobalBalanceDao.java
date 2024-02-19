package com.vulturi.trading.api.dao;

import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.balance.GlobalBalancePk;
import com.vulturi.trading.api.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface GlobalBalanceDao extends JpaRepository<GlobalBalance, GlobalBalancePk> {

    @Query("select globalBalance from GlobalBalance globalBalance where globalBalance.pk.ts >= :ts")
    Collection<GlobalBalance> findAllWithCreationLocalDateTimeAfter(
            @Param("ts") LocalDateTime ts);

}
