package com.vulturi.trading.api.services.withdraw;

import com.vulturi.exchanges.connector.model.Exchange;
import com.vulturi.exchanges.connector.service.ExchangeConnector;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.util.ExchangeCredentialsHandler;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractCryptoTransactionService {

    @Autowired
    private ExchangeCredentialsHandler exchangeCredentialsHandler;

    protected ExchangeConnector getExchangeConnector(Exchange exchange) throws ApiException {
        switch (exchange){
            case BINANCE_SPOT -> {
                 return ExchangeConnector.getExchangeConnector(exchange,exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount());
            }
            case KRAKEN_SPOT -> {
                return ExchangeConnector.getExchangeConnector(exchange,exchangeCredentialsHandler.getDecryptedCredentialsForKrakenAccount());
            }
        }
        throw new ApiException(ApiError.MISSING_VALUE);
    }

    protected ExchangeConnector getExchangeConnector(Exchange exchange, Account account) throws ApiException {
        switch (exchange){
            case BINANCE_SPOT -> {
                return ExchangeConnector.getExchangeConnector(exchange,exchangeCredentialsHandler.getDecryptedCredentialsForAccount(account));
            }
        }
        throw new ApiException(ApiError.MISSING_VALUE);
    }

}
