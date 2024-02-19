package com.vulturi.trading.api.services.account;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.AccountBalance;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.portfolio.PortfolioCreationRequest;
import com.vulturi.trading.api.models.user.Account;

import java.time.LocalDateTime;
import java.util.Collection;

public interface AccountService {

    GlobalBalance getGlobalBalance(String accountId) throws ApiException;

    void computeGlobalBalances(LocalDateTime ts) throws ApiException;
    Collection<AccountBalance> getHistoricalGlobalBalance(String accountId) throws ApiException;

    Account create(String accountId,String  email);
    Account get(String accountId);
    Collection<Account> findAll();
    void delete(String accountId);

    Account activate(String accountId) throws ApiException;

    Account save(Account account);

    Account createPortfolio(PortfolioCreationRequest portfolioCreationRequest) throws ApiException;




}
