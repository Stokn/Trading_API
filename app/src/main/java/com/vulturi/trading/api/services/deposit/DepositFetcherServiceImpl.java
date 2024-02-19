package com.vulturi.trading.api.services.deposit;

import com.google.common.collect.Maps;
import com.vulturi.exchanges.connector.model.ApiResult;
import com.vulturi.exchanges.connector.model.Deposit;
import com.vulturi.exchanges.connector.model.Exchange;
import com.vulturi.exchanges.connector.model.GetDepositRequest;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.deposit.ExchangeDeposit;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.services.withdraw.AbstractCryptoTransactionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class DepositFetcherServiceImpl extends AbstractCryptoTransactionService implements DepositFetcherService {
    @Autowired
    private AccountService accountService;

    private Map<String, Transaction> cacheByTransactionId = Maps.newConcurrentMap();

    @Autowired
    private PortfolioManagerService portfolioManagerService;

    @PostConstruct
    void init() throws ApiException {
        fetch();
    }
    @Scheduled(cron = "50 0/1 * * * *")
    @Override
    public void fetch() throws ApiException {
        log.info("Fetching deposit for accounts");
        Collection<Account> allAccounts = accountService.findAll();
        List<Account> activeAccounts = allAccounts.stream().filter(Account::isActive).toList();
        for (Account account : activeAccounts) {
            log.info("Fetching deposit for accountId :{}, subAccountId : {}", account.getAccountId(), account.getSubAccountId());
            GetDepositRequest getDepositRequest = new GetDepositRequest();
            getDepositRequest.setSubAccountEmail(account.getSubAccountEmail());
            ApiResult<Collection<Deposit>> deposit = getExchangeConnector(Exchange.BINANCE_SPOT, account).getDeposit(getDepositRequest);
            if (deposit.getSuccess()) {
                Collection<Deposit> data = deposit.getData();
                List<ExchangeDeposit> exchangeDeposits = data.stream().map(d -> this.toExchangeDeposit(account, d)).toList();

                for (ExchangeDeposit exchangeDeposit : exchangeDeposits) {
                    if(exchangeDeposit!=null){
                        portfolioManagerService.register(exchangeDeposit);
                    }
                }
            }
        }
    }

    private ExchangeDeposit toExchangeDeposit(Account account, Deposit deposit) {
        Transaction existingTransaction = portfolioManagerService.getByOperationId(deposit.getTxId());
        Portfolio existingTradingPortfolio = account
                .getPortfolios()
                .stream()
                .filter(portfolio -> portfolio.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0)
                .findAny()
                .orElse(null);
        log.info("Existing transaction {}",existingTransaction);
        log.info("Existing portfolio {}",existingTradingPortfolio);
        if (existingTradingPortfolio != null && existingTransaction == null) {
            log.info("Creating exchange deposit for account {} portfolioId {}", existingTradingPortfolio.getAccountId(), existingTradingPortfolio.getPortfolioId());
            ExchangeDeposit exchangeDeposit = new ExchangeDeposit();
            exchangeDeposit.setAccountId(account.getAccountId());
            exchangeDeposit.setAsset(deposit.getCoin());
            exchangeDeposit.setAmount(deposit.getAmount());
            exchangeDeposit.setTxId(deposit.getTxId());
            exchangeDeposit.setPortfolioId(existingTradingPortfolio.getPortfolioId());
            return exchangeDeposit;
        }
        return null;
    }

}
