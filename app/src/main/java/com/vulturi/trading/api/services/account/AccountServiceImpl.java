package com.vulturi.trading.api.services.account;

import com.google.common.collect.Maps;
import com.vulturi.exchanges.connector.model.Environment;
import com.vulturi.exchanges.connector.model.Exchange;
import com.vulturi.exchanges.connector.model.ExchangeCredentials;
import com.vulturi.trading.api.backend.binance.BinanceApiKeyCreationRequest;
import com.vulturi.trading.api.backend.binance.BinanceApiKeyCreationResponse;
import com.vulturi.trading.api.backend.binance.BinanceBroker;
import com.vulturi.trading.api.backend.binance.BinanceSubAccountCreationResponse;
import com.vulturi.trading.api.dao.AccountDao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.AccountBalance;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.portfolio.PortfolioCreationRequest;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.balance.GlobalBalanceService;
import com.vulturi.trading.api.util.ExchangeCredentialsHandler;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountServiceImpl implements AccountService {

    @Autowired
    private AccountDao dao;

    @Autowired
    private GlobalBalanceService globalBalanceService;

    @Autowired
    private ExchangeCredentialsHandler exchangeCredentialsHandler;

    private BinanceBroker binanceBrokerage = new BinanceBroker();

    private Map<String, Account> cache = Maps.newConcurrentMap();

    @PostConstruct
    void init() {
        cache = dao.findAll().stream().collect(Collectors.toMap(Account::getAccountId, a -> a));
        log.info("{} accounts stored in cache", cache.size());
    }

    @Scheduled(cron = "@hourly")
    private void computeBalance() throws ApiException {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        LocalDateTime adjustedNow = now.truncatedTo(ChronoUnit.HOURS);
        for (Account account : dao.findAll()) {
            globalBalanceService.snapshot(account, adjustedNow);
        }
        log.info("All global balance have been computed and stored for existing accounts");
    }

    public void computeGlobalBalances(LocalDateTime ts) throws ApiException {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        while (!ts.isAfter(now)) {
            for (Account account : cache.values()) {
                globalBalanceService.snapshot(account, ts);
            }
            log.info("Global balances computed as of {}", ts);
            ts = ts.plusHours(1);
        }
    }


    public GlobalBalance getGlobalBalance(String accountId) throws ApiException {
        Account account = get(accountId);
        if (account == null) {
            throw new ApiException(ApiError.ACCOUNT_DOES_NOT_EXIST);
        }
        return globalBalanceService.get(account, LocalDateTime.now(Clock.systemUTC()));
    }

    public Collection<AccountBalance> getHistoricalGlobalBalance(String accountId) throws ApiException {
        Account account = get(accountId);
        if (account == null) {
            throw new ApiException(ApiError.ACCOUNT_DOES_NOT_EXIST);
        }
        return globalBalanceService.getForAccount(account);
    }

    public Account create(String accountId, String email) {
        log.info("Creation account {} with email {}", accountId, email);
        Account account = new Account();
        account.setAccountId(accountId);
        account.setEmail(email);
        return save(account);
    }

    public Account get(String accountId) {
        return cache.get(accountId);
    }

    @Override
    public Collection<Account> findAll() {
        return cache.values();
    }


    @Override
    public void delete(String accountId) {
        Account account = get(accountId);
        if (account != null) {
            dao.delete(account);
            cache.remove(accountId);
        }
    }

    public Account save(Account account) {
        Account save = dao.save(account);
        cache.put(account.getAccountId(), account);
        return save;
    }

    @Override
    public Account createPortfolio(PortfolioCreationRequest portfolioCreationRequest) throws ApiException {
        if (portfolioCreationRequest.getPortfolioAccountType() == null || portfolioCreationRequest.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0) {
            throw new ApiException(ApiError.NULL_VALUE, "Portfolio account type cannot be null or TRADING");
        }
        Account existingAccount = cache.get(portfolioCreationRequest.getAccountId());
        if (existingAccount == null) {
            throw new ApiException(ApiError.NULL_VALUE, "Account: " + portfolioCreationRequest.getAccountId() + " does not exist");
        }

        if (!existingAccount.isActive()) {
            throw new ApiException(ApiError.NO_TRADING_PORTFOLIO_ACTIVATED);
        }

        if (existingAccount.getPortfolios().stream().map(Portfolio::getPortfolioId).toList().contains(portfolioCreationRequest.getPortfolioId())) {
            throw new ApiException(ApiError.NULL_VALUE, "Portfolio Id already exist for this account");
        }
        Portfolio portfolio = new Portfolio();
        portfolio.setPortfolioId(portfolioCreationRequest.getPortfolioId());
        portfolio.setAccountId(existingAccount.getAccountId());
        portfolio.setName(portfolio.getName());
        portfolio.setPortfolioAccountType(PortfolioAccountType.TRADING);
        ArrayList<Portfolio> existingPortfolios = new ArrayList<>(existingAccount.getPortfolios());
        existingPortfolios.add(portfolio);
        existingAccount.setPortfolios(existingPortfolios);
        return save(existingAccount);
    }

    public Account activate(String accountId) throws ApiException {
        Account existingAccount = cache.get(accountId);
        if (existingAccount != null) {
            BinanceSubAccountCreationResponse subAccount = createSubAccount(existingAccount);
            log.info("SubAccount was created: {}", subAccount);
            existingAccount.setSubAccountId(subAccount.getSubaccountId());
            existingAccount.setSubAccountEmail(subAccount.getEmail());
            existingAccount.setEncryptionKey(RandomStringUtils.randomAlphanumeric(32));
            log.info("Creating APIKey");
            BinanceApiKeyCreationResponse apiKeyCreationResponse = binanceBrokerage.createApiKeyForSubAccount(exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount(), BinanceApiKeyCreationRequest.builder().subAccountId(subAccount.getSubaccountId()).canTrade(false).build());
            log.info("APIKey have been created");
            exchangeCredentialsHandler.saveCredentials(existingAccount, ExchangeCredentials.builder().exchange(Exchange.BINANCE_SPOT).env(Environment.PROD).apiKey(apiKeyCreationResponse.getApiKey()).secret(apiKeyCreationResponse.getSecretKey()).build());
            existingAccount.setActive(true);
            log.info("Creation of trading portfolio for account id {}", existingAccount.getAccountId());
            Portfolio portfolio = new Portfolio();
            portfolio.setAccountId(existingAccount.getAccountId());
            portfolio.setName("MAIN_PORTFOLIO");
            portfolio.setPortfolioAccountType(PortfolioAccountType.TRADING);
            log.info("Portfolio Id : {} for AccountId {}", portfolio.getPortfolioId(), existingAccount.getAccountId());
            existingAccount.setPortfolios(Collections.singleton(portfolio));
            existingAccount.setTradingPortfolioId(portfolio.getPortfolioId());
            save(existingAccount);
            return existingAccount;
        }
        throw new ApiException(ApiError.NULL_VALUE, "Account: " + accountId + " does not exist");
    }

    private BinanceSubAccountCreationResponse createSubAccount(Account account) {
        log.info("Initiate the creation of the subAccount");
        ExchangeCredentials decryptedCredentialsForBrokerageAccount = exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount();
        log.info("Creating sub account for account : {}", account.getSubAccountId());
        int hash = account.getAccountId().hashCode();
        return binanceBrokerage.createSubAccountWithTag(decryptedCredentialsForBrokerageAccount, String.valueOf(hash));
    }

}
