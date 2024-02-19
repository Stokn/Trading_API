package com.vulturi.trading.api;

import com.vulturi.exchanges.connector.model.Exchange;
import com.vulturi.exchanges.connector.model.ExchangeCredentials;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.order.TradingOrder;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.portfolio.PortfolioCreationRequest;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionType;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.services.exchanges.MultiExchangeTradingService;
import com.vulturi.trading.api.util.ExchangeCredentialsHandler;
import com.vulturi.trading.api.web.dto.ConvertView;
import com.vulturi.trading.api.web.dto.TradingTransactionView;
import com.vulturi.trading.api.web.factory.ConvertFactory;
import com.vulturi.trading.api.web.factory.TradingTransactionFactory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class MultiExchangeTradingServiceTest {

    @MockBean
    private ExchangeCredentialsHandler exchangeCredentialsHandler;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MultiExchangeTradingService multiExchangeTradingService;

    @Autowired
    private PortfolioManagerService portfolioManagerService;

    @Autowired
    private TradingTransactionFactory tradingTransactionFactory;

    @Autowired
    private ConvertFactory convertFactory;

    private final static String accountId = "stokn-dev";
    private final static String email = "test@stokn.io";


    private static ExchangeCredentials exchangeCredentials = ExchangeCredentials.builder()
            .apiKey("vadETrtMNdyxQLQYR0uriL9UhhepHkzMFS3qQkZc8cqk6t4eEpWJ6SguCjQaCMYb")
            .secret("dPdQRfIajLGlmM3Qr67lfq2Q05E8mzQKwfZCN6XCtfk3GOtAI6oKGCJCCZCP55ph")
            .exchange(Exchange.BINANCE_SPOT)
            .build();

    private void before() throws ApiException{

        Mockito.when(exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount()).thenReturn(exchangeCredentials);

        Account account = accountService.create(accountId, email);
        Portfolio tradingPortfolio =new Portfolio();
        tradingPortfolio.setAccountId(accountId);
        tradingPortfolio.setName("MAIN_PORTFOLIO");
        tradingPortfolio.setPortfolioAccountType(PortfolioAccountType.TRADING);
        account.setActive(true);
        account.setTradingPortfolioId(tradingPortfolio.getPortfolioId());
        account.setPortfolios(new ArrayList<>(Collections.singleton(tradingPortfolio)));
        accountService.save(account);
        PortfolioCreationRequest portfolioCreationRequest = new PortfolioCreationRequest();
        portfolioCreationRequest.setName("Trading Fees");
        portfolioCreationRequest.setPortfolioAccountType(PortfolioAccountType.FEES);
        portfolioCreationRequest.setAccountId(accountId);
        portfolioCreationRequest.setPortfolioId("stokn-fees-dev");
        accountService.createPortfolio(portfolioCreationRequest);

        Transaction transaction = new Transaction();
        transaction.setTransactionSide(TransactionSide.CREDIT);
        transaction.setTransactionType(TransactionType.DEPOSIT);
        transaction.setAsset("USDT");
        transaction.setAmount(BigDecimal.valueOf(20));
        transaction.setPortfolioId(account.getTradingPortfolioId());
        transaction.setAccountId(accountId);
        portfolioManagerService.saveOrUpdate(transaction);
    }
    @Test
    public void get() throws ApiException, IOException {
        before();
        Account account = accountService.get(accountId);
        TradingOrder tradingOrder = new TradingOrder();
        tradingOrder.setFromAsset("USDT");
        tradingOrder.setToAsset("ETH");
        tradingOrder.setAccountId(accountId);
        tradingOrder.setQuantity(BigDecimal.valueOf(20));
        tradingOrder.setExchange(ExchangePlatform.BINANCE);
        Collection<TradingTransaction> tradingTransactions = multiExchangeTradingService.placeOrder(account, tradingOrder);
        List<TradingTransactionView> tradingTransactionViews = tradingTransactions.stream().map(tradingTransactionFactory::toTradingTransactionView).toList();
        List<ConvertView> convertViews = tradingTransactions.stream().map(convertFactory::toConvertView).toList();
    }
}
