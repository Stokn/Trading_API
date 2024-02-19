package com.vulturi.trading.api.dao;


import com.vulturi.trading.api.models.transaction.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;

public interface TransactionDao extends JpaRepository<Transaction, String> {

    Collection<Transaction> findByAccountId(String accountId);
    @Query("select transaction from Transaction transaction where transaction.creationTs >= :ts")
    Collection<Transaction> findAllWithCreationLocalDateTimeAfter(
            @Param("ts") LocalDateTime ts);


    @Query("from Transaction transaction where transaction.creationTs BETWEEN :minTs AND :maxTs")
    Collection<Transaction> findAllBetweenTs(
            @Param("minTs") LocalDateTime minTs, @Param("maxTs") LocalDateTime maxTs);

    @Query("from Transaction transaction where transaction.accountId in :accountIds")
    Collection<Transaction> findByUserAccountIds(Collection<String> accountIds);

    @Query("from Transaction transaction where transaction.operationId in :operationIds")
    Collection<Transaction> findByOperationIds(Collection<String> operationIds);
}