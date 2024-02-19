package com.vulturi.trading.api.web.dto.deposit;

import lombok.Data;

@Data
public class DepositAddressView {
    private String coin;
    private String network;
    private String address;
}
