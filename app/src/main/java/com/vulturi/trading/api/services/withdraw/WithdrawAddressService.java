package com.vulturi.trading.api.services.withdraw;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.withdraw.WithdrawAddress;

import java.util.Collection;

public interface WithdrawAddressService {

    void saveOrUpdate(WithdrawAddress withdrawAddress) throws ApiException;

    Collection<WithdrawAddress> getByAccountId(String accountId);
    WithdrawAddress get(String address);
}
