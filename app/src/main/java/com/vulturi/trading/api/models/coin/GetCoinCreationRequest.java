package com.vulturi.trading.api.models.coin;

import lombok.Data;

import java.util.Collection;

@Data
public class GetCoinCreationRequest {
    private String name;
    private String slug;
    private String logoUrl;
    private TypeDeposit typeDeposit = TypeDeposit.CRYPTO;
    private Collection<String> availableNetworks;
}
