package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.web.dto.TransactionView;

public class TransactionFactory {
    public static Transaction toTransaction(TransactionView view){
        Transaction transaction = new Transaction();
        transaction.setTransactionType(view.getTransactionType());
        transaction.setTransactionSide(view.getTransactionSide());
        transaction.setAmount(view.getAmount());
        transaction.setAsset(view.getAsset());
        transaction.setAccountId(view.getAccountId());
        transaction.setPortfolioId(view.getPortfolioId());
        return transaction;
    }
}
