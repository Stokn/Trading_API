package com.vulturi.trading.api.backend.binance;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class BinanceIpRestrictionResponse {

    private String subAccountId;
    private List<String> ipList;
    private Boolean ipRestrict;
}
