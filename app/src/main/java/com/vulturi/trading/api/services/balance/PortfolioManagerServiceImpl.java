package com.vulturi.trading.api.services.balance;

import com.google.common.collect.Maps;

import com.vulturi.trading.api.dao.TransactionDao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.deposit.ExchangeDeposit;
import com.vulturi.trading.api.models.exchange.TradingFees;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionFilter;
import com.vulturi.trading.api.models.transaction.TransactionType;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PortfolioManagerServiceImpl implements PortfolioManagerService {


    @Value("${stokn.account-id}")
    private String stoknAccountId;

    @Value("${stokn.fees-portfolio-id}")
    private String portfolioIdFees;


    @Autowired
    private TransactionDao transactionDao;
    private Map<String, List<Transaction>> cacheByUserAccountId = Maps.newConcurrentMap();
    private Map<String, List<Transaction>> cacheByOperationId = Maps.newConcurrentMap();
    private LocalDateTime minTsInCache;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    @Scheduled(cron = "@daily")
    public void refreshCache() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        minTsInCache = now.minusDays(30);
        List<Transaction> transactionList = transactionDao.findAllWithCreationLocalDateTimeAfter(minTsInCache)
                .stream().toList();
        cacheByUserAccountId = transactionList.stream().filter(tx -> tx.getAccountId() != null).collect(Collectors.groupingBy(Transaction::getAccountId));
        cacheByOperationId = transactionList.stream().collect(Collectors.groupingBy(Transaction::getOperationId));
        log.info("{} crypto ledger saved in cache", cacheByOperationId.size());
        log.info("minTsInCache {}", minTsInCache);
    }


    @Override
    public Collection<Transaction> filter(TransactionFilter filter) throws ApiException {
        List<Transaction> transactions = new ArrayList<>();
        if (filter.getMinTs() == null) {

            transactions.addAll(transactionDao.findByUserAccountIds(filter.getAccountIds())
                    .stream()
                    .filter(filter.toPredicate())
                    .toList());
        } else {
            if (filter.getMinTs().compareTo(minTsInCache) > 0) {
                transactions.addAll(cacheByOperationId.values().stream().flatMap(Collection::stream).filter(filter.toPredicate()).toList());
                if (transactions.size() != 0) {
                    return transactions;
                }
                throw new ApiException(ApiError.CANNOT_FIND_ANY_WALLET_TRANSACTION);
            }
            transactions.addAll(transactionDao.findAllBetweenTs(filter.getMinTs(), filter.getMaxTs())
                    .stream()
                    .filter(filter.toPredicate())
                    .toList());
        }
        return transactions;

    }

    @Override
    public void delete(String id) {
        Transaction byId = transactionDao.findById(id).orElse(null);
        if(byId!=null){
            transactionDao.delete(byId);
            refreshCache();
        }

    }

    @Override
    public Transaction saveOrUpdate(Transaction transaction) {
        Transaction saveTx = transactionDao.save(transaction);
        cacheByOperationId.computeIfAbsent(transaction.getOperationId(), k -> new ArrayList<>());
        cacheByOperationId.get(transaction.getOperationId()).add(transaction);
        cacheByUserAccountId.computeIfAbsent(transaction.getAccountId(), k -> new ArrayList<>());
        cacheByUserAccountId.get(transaction.getAccountId()).add(transaction);
        log.info("Transaction {} has been saved in db and cache", transaction.getId());
        return saveTx;
    }

    @Override
    public Collection<Transaction> findByOperationId(String operationId) {
        return cacheByOperationId.get(operationId);
    }

    @Override
    public Collection<Transaction> get(String accountId) {
        return transactionDao.findByAccountId(accountId);
    }

    @Override
    public List<Transaction> saveAll(Collection<Transaction> ledgerCollection) {
        List<Transaction> transactions = ledgerCollection.stream().map(this::saveOrUpdate).toList();
        return transactions;
    }

    @Override
    public Collection<Transaction> findByOperationIds(Collection<String> operationIds) {
        return transactionDao.findByOperationIds(operationIds);
    }


    @Override
    public void register(WithdrawResponse withdrawResponse) {
        Transaction toDebit = new Transaction();
        toDebit.setAccountId(withdrawResponse.getAccountId());
        toDebit.setPortfolioId(withdrawResponse.getPortfolioId());
        toDebit.setAmount(withdrawResponse.getAmount());
        toDebit.setAsset(withdrawResponse.getAsset());
        toDebit.setOperationId(withdrawResponse.getOperationId());
        toDebit.setTransactionSide(TransactionSide.DEBIT);
        toDebit.setTransactionType(TransactionType.WITHDRAW);
        saveOrUpdate(toDebit);
    }

    @Override
    public void register(TradingFees tradingFees) {
        Transaction toDebitFees = new Transaction();
        Transaction toCreditFees = new Transaction();

        toDebitFees.setPortfolioId(tradingFees.getOriginPortfolioId());
        toDebitFees.setAccountId(tradingFees.getOriginAccountId());
        toDebitFees.setAmount(tradingFees.getQuantity());
        toDebitFees.setAsset(tradingFees.getAsset());
        toDebitFees.setTransactionSide(TransactionSide.DEBIT);
        toDebitFees.setTransactionType(TransactionType.FEES);
        toDebitFees.setOperationId(tradingFees.getTradingTransactionId());

        toCreditFees.setPortfolioId(portfolioIdFees);
        toCreditFees.setAccountId(stoknAccountId);
        toCreditFees.setAmount(tradingFees.getQuantity());
        toCreditFees.setAsset(tradingFees.getAsset());
        toCreditFees.setTransactionSide(TransactionSide.CREDIT);
        toCreditFees.setTransactionType(TransactionType.FEES);
        toCreditFees.setOperationId(tradingFees.getTradingTransactionId());

        saveAll(Arrays.asList(toDebitFees,toCreditFees));
    }

    // do the same for deposit


    @Override
    public void register(ExchangeDeposit exchangeDeposit) {
        Transaction transaction = new Transaction();
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setTransactionSide(TransactionSide.CREDIT);
        transaction.setAccountId(exchangeDeposit.getAccountId());
        transaction.setAsset(exchangeDeposit.getAsset());
        transaction.setAmount(exchangeDeposit.getAmount());
        transaction.setOperationId(exchangeDeposit.getTxId());
        transaction.setPortfolioId(exchangeDeposit.getPortfolioId());
        saveOrUpdate(transaction);
    }

    @Override
    public void registerAll(Collection<ExchangeDeposit> exchangeDeposits) {
        for (ExchangeDeposit exchangeDeposit : exchangeDeposits) {
            register(exchangeDeposit);
        }
    }

    @Override
    public Transaction getByOperationId(String operationId) {
        List<Transaction> transactions = cacheByOperationId.get(operationId);
        if(transactions!=null){
            return transactions.stream().findFirst().orElse(null);
        }
        return null;
    }

}
