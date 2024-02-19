package com.vulturi.trading.api.services.deposit;

import com.vulturi.exchanges.connector.model.*;
import com.vulturi.exchanges.connector.service.ExchangeConnector;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.services.user.UserService;
import com.vulturi.trading.api.services.withdraw.AbstractCryptoTransactionService;
import com.vulturi.trading.api.util.ExchangeCredentialsHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class DepositServiceImpl extends AbstractCryptoTransactionService implements DepositService {
    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;
    @Autowired
    private ExchangeCredentialsHandler exchangeCredentialsHandler;

    @Autowired
    private CoinService coinService;


    public Collection<Deposit> getHistoricalDeposit(String accountId) throws ApiException {
        ApiResult<Collection<Deposit>> apiResult = getDepositHistoryFromApi(accountId);
        if (apiResult.getSuccess()) {
            return apiResult.getData();
        }
        throw new ApiException(409, apiResult.getErrorMessage());
    }

    private ApiResult<Collection<Deposit>> getDepositHistoryFromApi(String accountId) throws ApiException {
        Account account = getAccount(accountId);
        GetDepositRequest getDepositRequest = new GetDepositRequest();
        getDepositRequest.setSubAccountEmail(accountId);
        return getExchangeConnector(Exchange.BINANCE_SPOT,account).getDeposit(getDepositRequest);
    }


    private Account getAccount(String accountId) throws ApiException {
        Account account = accountService.get(accountId);
        if (account != null) {
            return account;
        }
        throw new ApiException(ApiError.NULL_VALUE, "Account: " + accountId + " does not exist");
    }


    private boolean isAvailableForDeposit(String asset, String network) throws ApiException {
        Coin existingCoin = coinService.findBySlug(asset);
        if(existingCoin==null){
            throw  new ApiException(ApiError.COIN_DOES_NOT_EXIST);
        }
        return existingCoin.getAvailableNetworks().contains(network);
    }

    @Override
    public ExchangeAddress findByAssetAndBlockchain(String accountId, String asset, String network) throws ApiException {
        log.info("Looking for address for");
        if(isAvailableForDeposit(asset,network)){
            Account account = getAccount(accountId);
            GetDepositAddress getDepositAddress = new GetDepositAddress();
            getDepositAddress.setCoin(asset);
            getDepositAddress.setNetwork(network);
            ExchangeConnector exchangeConnector = getExchangeConnector(Exchange.BINANCE_SPOT, account);
            ApiResult<ExchangeAddress> depositAddress = exchangeConnector.getDepositAddress(getDepositAddress);
            if (depositAddress.getSuccess()) {
                return depositAddress.getData();
            }
            throw new ApiException(409, depositAddress.getErrorCode(), depositAddress.getErrorMessage());
        }
        throw new ApiException(ApiError.NETWORK_IS_NOT_AVAILABLE);
    }

    @Override
    public void findByAccountId(String accountId) {
    }

    @Override
    public Collection<String> findNetworkForAsset(String asset) throws ApiException {
        ApiResult<Collection<CoinInfo>> allCoinInfo = getAllAssetInfo();
        CoinInfo tgtCoinInfo = allCoinInfo
                .getData()
                .stream()
                .filter(coinInfo -> coinInfo.getCoin().equals(asset))
                .findFirst().orElseThrow(
                        () -> new ApiException(ApiError.NULL_VALUE, "Cannot find network for asset:" + asset)
                );
        return tgtCoinInfo.getNetworks().stream().map(CoinNetwork::getValue).toList();
    }

    private ApiResult<Collection<CoinInfo>> getAllAssetInfo() throws ApiException {
        return getExchangeConnector(Exchange.BINANCE_SPOT).getAllCoinInfo();
    }
}
