package com.vulturi.trading.api.services.exchanges;

import com.vulturi.exchanges.connector.model.Exchange;
import com.vulturi.exchanges.connector.service.ExchangeConnector;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionType;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.util.ExchangeCredentialsHandler;
import com.vulturi.trading.api.util.SpringCtx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;

public abstract class AbstractExchangeService {


    @Value("${stokn.account-id}")
    protected String stoknAccountId;

    @Value("${stokn.fees-portfolio-id}")
    protected String portfolioIdFees;

    @Autowired
    protected PortfolioManagerService portfolioManagerService;

    @Autowired
    private ExchangeCredentialsHandler exchangeCredentialsHandler;


    static TradingService builder(ExchangePlatform exchange) {
        switch (exchange) {
            case KRAKEN -> {
                return SpringCtx.getAppCtx().getBean(KrakenExchangeService.class);
            }
            case BINANCE -> {
                return SpringCtx.getAppCtx().getBean(BinanceExchangeService.class);
            }
        }
        return null;
    }


    public ExchangeConnector getExchangeConnector() {
        ExchangePlatform exchangePlatform = getExchangePlatform();
        switch (exchangePlatform) {
            case KRAKEN -> {
                return ExchangeConnector.getExchangeConnector(Exchange.KRAKEN_SPOT, exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount());
            }
            case BINANCE -> {
                return ExchangeConnector.getExchangeConnector(Exchange.BINANCE_SPOT, exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount());
            }
        }
        return null;
    }

    protected Transaction toTransaction(String accountId, String portfolioId, String operationId, TransactionSide transactionSide, TransactionType transactionType, String asset, BigDecimal amount){
        Transaction transaction =new Transaction();
        transaction.setAccountId(accountId);
        transaction.setPortfolioId(portfolioId);
        transaction.setOperationId(operationId);
        transaction.setTransactionSide(transactionSide);
        transaction.setTransactionType(transactionType);
        transaction.setAsset(asset);
        transaction.setAmount(amount);
        return transaction;
    }

    protected abstract ExchangePlatform getExchangePlatform();


}
