package com.vulturi.trading.api.services.balance;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.AccountBalance;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.user.Account;

import java.time.LocalDateTime;
import java.util.Collection;

public interface GlobalBalanceService {
    GlobalBalance get(Account account, LocalDateTime ts) throws ApiException;
    Collection<AccountBalance> getForAccount(Account account) throws ApiException;
    void snapshot(Account account, LocalDateTime ts) throws ApiException;
}
