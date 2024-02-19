package com.vulturi.trading.api.models.withdraw;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class WithdrawResponse {
    private String accountId;
    private BigDecimal amount;
    private String asset;
    private String network;
    private String address;
    private Boolean success = Boolean.TRUE;
    private String message;
    private String portfolioId;
    private String operationId = UUID.randomUUID().toString();
}
