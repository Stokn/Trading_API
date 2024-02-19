package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.web.dto.TradingTransactionView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class TradingTransactionFactory {

    @Autowired
    private PortfolioManagerService portfolioManagerService;


    public TradingTransactionView toTradingTransactionView(TradingTransaction tradingTransaction){
        TradingTransactionView view = new TradingTransactionView();
        Collection<Transaction> byOperationId = portfolioManagerService.findByOperationId(tradingTransaction.getId());
        view.setTransactions(byOperationId);
        view.setId(tradingTransaction.getId());
        view.setTs(tradingTransaction.getPk().getTs());
        view.setPortfolioId(tradingTransaction.getPk().getPortfolioId());
        view.setAccountId(tradingTransaction.getPk().getAccountId());
        view.setFromCoin(tradingTransaction.getFromCoin());
        view.setOrderId(tradingTransaction.getOrderId());
        view.setToCoin(tradingTransaction.getToCoin());
        return view;
    }



}
