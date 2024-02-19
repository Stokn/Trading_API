package com.vulturi.trading.api.models.withdraw;

import com.vulturi.trading.api.backend.scorechain.Blockchain;
import lombok.Data;

@Data
public class WithdrawAddressCreationRequest {
    private String accountId;
    private String coin;
    private String address;
    private Blockchain blockchain;
}
