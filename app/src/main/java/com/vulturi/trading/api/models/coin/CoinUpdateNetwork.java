package com.vulturi.trading.api.models.coin;

import lombok.Data;

import java.util.Collection;

@Data
public class CoinUpdateNetwork {
    private String slug;
    private Collection<String> network;
}
