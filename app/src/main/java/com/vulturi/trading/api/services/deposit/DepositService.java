package com.vulturi.trading.api.services.deposit;


import com.vulturi.exchanges.connector.model.Deposit;
import com.vulturi.exchanges.connector.model.ExchangeAddress;
import com.vulturi.exchanges.connector.model.GetDepositRequest;
import com.vulturi.trading.api.exceptions.ApiException;

import java.util.Collection;

public interface DepositService {

    ExchangeAddress findByAssetAndBlockchain(String subAccountId, String asset, String network) throws ApiException;

    void findByAccountId(String accountId);

    Collection<String> findNetworkForAsset(String asset) throws ApiException;

    Collection<Deposit> getHistoricalDeposit(String accountId) throws ApiException;

}
