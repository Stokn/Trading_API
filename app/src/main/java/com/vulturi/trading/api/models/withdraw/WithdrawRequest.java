package com.vulturi.trading.api.models.withdraw;

import com.vulturi.trading.api.backend.scorechain.Network;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class WithdrawRequest {
    private String accountId;
    private Network network;
    private String address;
    private String asset;
    private BigDecimal amount;
}
