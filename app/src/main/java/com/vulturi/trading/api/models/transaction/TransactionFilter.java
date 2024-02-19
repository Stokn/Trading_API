package com.vulturi.trading.api.models.transaction;

import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.function.Predicate;

@Getter
@Setter
@Builder
@ToString
public class TransactionFilter {
    private Collection<String> accountIds;

    private Collection<String> assets;
    private LocalDateTime minTs;
    private LocalDateTime maxTs;
    private TransactionType transactionType;
    private TransactionSide transactionSide;
    private Collection<String> portfolioIds;


    public TransactionFilter(Collection<String> accountIds, Collection<String> assets, LocalDateTime minTs, LocalDateTime maxTs, TransactionType transactionType, TransactionSide transactionSide, Collection<String> portfolioIds) {

        this.accountIds = accountIds;
        this.assets = assets;
        this.minTs = minTs;
        this.maxTs = maxTs;
        this.transactionType = transactionType;
        this.transactionSide = transactionSide;
        this.portfolioIds = portfolioIds;
    }


    private Predicate<Transaction> getAccountIdPredicate() {
        return this.accountIds != null && !this.accountIds.isEmpty() ? (s) -> {
            return this.accountIds.contains(s.getAccountId());
        } : (p) -> {
            return true;
        };
    }


    private Predicate<Transaction> getPortfolioIdPredicate() {
        return this.portfolioIds != null && !this.portfolioIds.isEmpty() ? (s) -> {
            return this.portfolioIds.contains(s.getAccountId());
        } : (p) -> {
            return true;
        };
    }

    private Predicate<Transaction> getAssetsPredicate() {
        return this.assets != null && !this.assets.isEmpty() ? (s) -> {
            return this.assets.contains(s.getAsset());
        } : (p) -> {
            return true;
        };
    }


    private Predicate<Transaction> getTransactionTypePredicate() {
        return this.transactionType != null && !this.accountIds.isEmpty() ? (s) -> {
            return this.transactionType.equals(s.getTransactionType());
        } : (p) -> {
            return true;
        };
    }

    private Predicate<Transaction> getTransactionSidePredicate() {
        return this.transactionSide != null && !this.accountIds.isEmpty() ? (s) -> {
            return this.transactionSide.equals(s.getTransactionSide());
        } : (p) -> {
            return true;
        };
    }

    private Predicate<Transaction> getMinTsPredicate() {
        return this.minTs != null ? (s) -> {
            return s.getCreationTs().compareTo(minTs) >= 0;
        } : (p) -> {
            return true;
        };
    }


    private Predicate<Transaction> getMaxTsPredicate() {
        return this.maxTs != null ? (s) -> {
            return s.getCreationTs().compareTo(maxTs) <= 0;
        } : (p) -> {
            return true;
        };
    }

    public Predicate<Transaction> toPredicate() {
        return this.getAccountIdPredicate()
                .and(this.getPortfolioIdPredicate())
                .and(this.getAssetsPredicate())
                .and(this.getMaxTsPredicate())
                .and(this.getMinTsPredicate())
                .and(this.getTransactionTypePredicate())
                .and(this.getTransactionSidePredicate());
    }


}