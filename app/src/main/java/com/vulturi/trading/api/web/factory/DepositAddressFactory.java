package com.vulturi.trading.api.web.factory;

import com.vulturi.exchanges.connector.model.ExchangeAddress;
import com.vulturi.trading.api.web.dto.deposit.DepositAddressView;

public class DepositAddressFactory {
    public static DepositAddressView toView(ExchangeAddress exchangeAddress){
        DepositAddressView depositAddressView = new DepositAddressView();
        depositAddressView.setAddress(exchangeAddress.getAddress());
        depositAddressView.setCoin(exchangeAddress.getCoin());
        depositAddressView.setNetwork(exchangeAddress.getNetwork());
        return depositAddressView;
    }
}
