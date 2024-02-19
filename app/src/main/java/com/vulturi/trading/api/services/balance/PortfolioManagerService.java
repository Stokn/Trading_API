package com.vulturi.trading.api.services.balance;


import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.deposit.ExchangeDeposit;
import com.vulturi.trading.api.models.exchange.TradingFees;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionFilter;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;

import java.util.Collection;
import java.util.List;

public interface PortfolioManagerService {


    Collection<Transaction> filter(TransactionFilter filter) throws ApiException;

    void delete(String id);

    Transaction saveOrUpdate(Transaction transaction);

    Collection<Transaction> findByOperationId(String operationId);

    Collection<Transaction> get(String accountId);

    List<Transaction> saveAll(Collection<Transaction> cryptoLedgerCollection);

    Collection<Transaction> findByOperationIds(Collection<String> operationIds);

    void register(WithdrawResponse withdrawResponse);

    void register (TradingFees tradingFees);


    void register(ExchangeDeposit exchangeDeposit);

    void registerAll(Collection<ExchangeDeposit> exchangeDeposits);

    Transaction getByOperationId(String operationId);




}
