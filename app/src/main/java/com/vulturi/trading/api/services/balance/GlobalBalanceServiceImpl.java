package com.vulturi.trading.api.services.balance;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.GlobalBalanceDao;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.*;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionFilter;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.marketplace.BinanceService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class GlobalBalanceServiceImpl implements GlobalBalanceService {

    @Autowired
    private PortfolioManagerService portfolioManagerService;


    @Autowired
    private GlobalBalanceDao dao;

    @Autowired
    private BinanceService binanceService;


    private Map<String, List<GlobalBalance>> cacheByAccountId = Maps.newConcurrentMap();

    @PostConstruct
    void init() {

        Collection<GlobalBalance> allWithCreationLocalDateTimeAfter = dao.findAllWithCreationLocalDateTimeAfter(LocalDateTime.now(Clock.systemUTC()).minusMonths(6));
        cacheByAccountId = allWithCreationLocalDateTimeAfter.stream().collect(Collectors.groupingBy(globalBalance -> globalBalance.getPk().getAccountId()));
    }

    @Override
    public GlobalBalance get(Account account, LocalDateTime ts) throws ApiException {
        String accountId = account.getAccountId();
        Map<String, PortfolioAccountType> portfolioAccountTypeMap = account.getPortfolios().stream().collect(Collectors.toMap(Portfolio::getPortfolioId, Portfolio::getPortfolioAccountType));
        TransactionFilter transactionFilter = TransactionFilter.builder().accountIds(Collections.singleton(accountId)).maxTs(ts).build();
        Collection<Transaction> transactions = portfolioManagerService.filter(transactionFilter);
        GlobalBalance globalBalance = new GlobalBalance();
        globalBalance.getPk().setAccountId(accountId);
        globalBalance.getPk().setTs(ts);
        Map<String, List<Transaction>> transactionByPortfolioId = transactions.stream().collect(Collectors.groupingBy(Transaction::getPortfolioId));
        Collection<PortfolioBalance> portfolioBalances = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : transactionByPortfolioId.entrySet()) {
            String portfolioId = entry.getKey();
            List<Transaction> transactionList = entry.getValue();
            PortfolioBalance portfolioBalance = computePortfolioBalance(ts, portfolioId, transactionList);
            portfolioBalance.getPk().setAccountId(accountId);
            portfolioBalance.getPk().setTs(ts);
            portfolioBalance.setPortfolioAccountType(portfolioAccountTypeMap.get(portfolioId));
            portfolioBalances.add(portfolioBalance);
        }
        globalBalance.setPortfolioBalances(portfolioBalances);
        return globalBalance;
    }

    @Override
    public Collection<AccountBalance> getForAccount(Account account) throws ApiException {
        List<GlobalBalance> globalBalances = cacheByAccountId.get(account.getAccountId());
        Collection<AccountBalance> accountBalances = new ArrayList<>();
        for (GlobalBalance globalBalance : globalBalances) {
            PortfolioBalance existingPortfolioBalance = globalBalance.getPortfolioBalances().stream().filter(portfolioBalance -> portfolioBalance.getPortfolioAccountType().equals(PortfolioAccountType.TRADING)).findFirst().orElse(null);
            if (existingPortfolioBalance != null && existingPortfolioBalance.getPk().getTs().getHour() == 0) {
                BigDecimal sumPtfValue = existingPortfolioBalance.getAssetBalances().stream().map(AssetBalance::getEurValue).filter(Objects::nonNull).reduce(BigDecimal::add).orElse(null);
                AccountBalance accountBalance = new AccountBalance();
                accountBalance.setAmountEUR(sumPtfValue);
                accountBalance.setAccountId(account.getAccountId());
                accountBalance.setTs(globalBalance.getPk().getTs());
                accountBalances.add(accountBalance);
            }
        }
        return accountBalances;
    }

    @Override
    public void snapshot(Account account, LocalDateTime ts) throws ApiException {
        GlobalBalance existingGlobalBalance = cacheByAccountId.get(account.getAccountId()).stream().filter(globalBalance -> globalBalance.getPk().getTs().compareTo(ts) == 0).findFirst().orElse(null);
        if (existingGlobalBalance == null) {
            GlobalBalance globalBalance = get(account, ts);
            dao.save(globalBalance);
        }

    }

    private PortfolioBalance computePortfolioBalance(LocalDateTime ts, String portfolioId, List<Transaction> transactions) throws ApiException {
        PortfolioBalance portfolioBalance = new PortfolioBalance();
        portfolioBalance.getPk().setPortfolioId(portfolioId);
        Map<String, List<Transaction>> transactionByAsset = transactions.stream().collect(Collectors.groupingBy(Transaction::getAsset));
        Collection<AssetBalance> assetBalances = new ArrayList<>();
        for (Map.Entry<String, List<Transaction>> entry : transactionByAsset.entrySet()) {
            String asset = entry.getKey();
            List<Transaction> transactionList = entry.getValue();
            AssetBalance assetBalance = computeAssetBalance(ts, portfolioId, asset, transactionList);
            assetBalances.add(assetBalance);
        }
        portfolioBalance.setAssetBalances(assetBalances);
        return portfolioBalance;
    }

    private AssetBalance computeAssetBalance(LocalDateTime ts, String portfolioId, String asset, List<Transaction> transactions) throws ApiException {
        Collection<Transaction> debitTransaction = transactions.stream().filter(transaction -> transaction.getTransactionSide().compareTo(TransactionSide.DEBIT) == 0).toList();
        Collection<Transaction> creditTransaction = transactions.stream().filter(transaction -> transaction.getTransactionSide().compareTo(TransactionSide.CREDIT) == 0).toList();
        BigDecimal totalDebit = debitTransaction.stream().map(Transaction::getAmount).reduce(BigDecimal::add).orElse(null);
        BigDecimal totalCredit = creditTransaction.stream().map(Transaction::getAmount).reduce(BigDecimal::add).orElse(null);
        AssetBalance assetBalance = null;
        AssetBalancePk pk = AssetBalancePk.builder().asset(asset).portfolioId(portfolioId).ts(ts).build();
        BigDecimal lastPriceEUR = binanceService.getLastPriceEUR(asset);
        if (totalCredit != null) {
            if (totalDebit != null) {
                BigDecimal quantity = totalCredit.subtract(totalDebit);
                assetBalance = new AssetBalance(pk, quantity, lastPriceEUR == null ? null : quantity.multiply(lastPriceEUR), null);
            }
            if (totalDebit == null) {
                BigDecimal quantity = totalCredit;
                assetBalance = new AssetBalance(pk, quantity, lastPriceEUR == null ? null : quantity.multiply(lastPriceEUR), null);
            }
        } else if (totalDebit != null) {
            BigDecimal quantity = BigDecimal.ZERO.subtract(totalDebit);
            assetBalance = new AssetBalance(pk, quantity, lastPriceEUR == null ? null : quantity.multiply(lastPriceEUR), null);
        }
        return assetBalance;
    }


}
