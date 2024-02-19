package com.vulturi.trading.api.services.exchanges;


import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.AssetBalance;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.balance.PortfolioBalance;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.order.TradingOrder;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.balance.GlobalBalanceService;
import com.vulturi.trading.api.services.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Slf4j
public class MultiExchangeTradingServiceImpl implements MultiExchangeTradingService {
    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GlobalBalanceService globalBalanceService;

    private boolean canTrade(User user, TradingOrder order) {
        return user.getAccountIds().contains(order.getAccountId());
    }

    private boolean enoughCoinForTrading(TradingOrder order) throws ApiException {
        Account account = accountService.get(order.getAccountId());
        if (account == null) {
            throw new ApiException(ApiError.ACCOUNT_DOES_NOT_EXIST);
        }
        GlobalBalance globalBalance = globalBalanceService.get(account, LocalDateTime.now(Clock.systemUTC()));
        PortfolioBalance existingPortfolioBalance = globalBalance.getPortfolioBalances().stream().filter(portfolioBalance -> portfolioBalance.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findAny().orElse(null);
        if (existingPortfolioBalance != null) {
            AssetBalance existingAssetBalance = existingPortfolioBalance.getAssetBalances().stream().filter(assetBalance -> assetBalance.getPk().getAsset().compareTo(order.getFromAsset()) == 0).findAny().orElse(null);
            if (existingAssetBalance != null) {
                return existingAssetBalance.getQuantity().compareTo(order.getQuantity()) >= 0;
            }
            return false;
        }
        return false;
    }

    @Override
    public Collection<TradingTransaction> placeOrder(Account account, TradingOrder tradingOrder) throws ApiException {
        if(!account.isActive()){
            throw new ApiException(ApiError.NO_TRADING_PORTFOLIO_ACTIVATED);
        }

        if (enoughCoinForTrading(tradingOrder)) {

            tradingOrder.setPortfolioId(account.getTradingPortfolioId());
            return placeOrder(tradingOrder);
        }
        throw new ApiException(ApiError.NOT_ENOUGH_COIN_FOR_TRADING);
    }



    private Collection<TradingTransaction> placeOrder(TradingOrder tradingOrder) throws ApiException {
        Collection<TradingTransaction> tradingTransactions = new ArrayList<>();
        Map<TradingOrder, ExchangePlatform> orderExchangeMap = rootTradingOrder(tradingOrder);
        for (Map.Entry<TradingOrder, ExchangePlatform> entry : orderExchangeMap.entrySet()) {
            TradingOrder tradingRequestToExecute = entry.getKey();
            ExchangePlatform exchange = entry.getValue();
            TradingTransaction tradingTransaction = Objects.requireNonNull(AbstractExchangeService.builder(exchange)).trade(tradingRequestToExecute);
            tradingTransactions.add(tradingTransaction);
        }
        return tradingTransactions;
    }

    private static boolean hasDefaultExchange(TradingOrder tradingOrder) {
        return tradingOrder.getExchange() != null;
    }

    private Map<TradingOrder, ExchangePlatform> rootTradingOrder(TradingOrder tradingOrder) {
        Map<TradingOrder, ExchangePlatform> tradingOrderExchangeMap = new HashMap<>();
        if (hasDefaultExchange(tradingOrder)) {
            return Map.of(tradingOrder, tradingOrder.getExchange());
        }
        // todo find the possible root
        return Map.of();
    }


}
