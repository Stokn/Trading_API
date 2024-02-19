package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionType;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.web.dto.ConvertView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class ConvertFactory {
    @Autowired
    private PortfolioManagerService portfolioManagerService;

    public ConvertView toConvertView(TradingTransaction tradingTransaction){
        Collection<Transaction> byOperationId = portfolioManagerService.findByOperationId(tradingTransaction.getId());
        ConvertView convertView = new ConvertView();
        convertView.setAccountId(tradingTransaction.getPk().getAccountId());
        convertView.setPortfolioId(tradingTransaction.getPk().getPortfolioId());
        convertView.setFrom(byOperationId.stream().filter(transaction -> transaction.getTransactionSide().compareTo(TransactionSide.DEBIT)==0&&transaction.getTransactionType().compareTo(TransactionType.TRADE)==0).findFirst().orElse(null));
        convertView.setTo(byOperationId.stream().filter(transaction -> transaction.getTransactionSide().compareTo(TransactionSide.CREDIT)==0&&transaction.getTransactionType().compareTo(TransactionType.TRADE)==0).findFirst().orElse(null));
        convertView.setFees(byOperationId.stream().filter(transaction -> transaction.getTransactionSide().compareTo(TransactionSide.DEBIT)==0&&transaction.getTransactionType().compareTo(TransactionType.FEES)==0).findFirst().orElse(null));
        return convertView;
    }

}
