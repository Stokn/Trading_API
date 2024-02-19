package com.vulturi.trading.api.models.deposit;

import lombok.Data;

@Data
public class UserBankDetails {
    private String userId;
    private BankInfo bankInfo;
    private String bankingTransferRefId;
}
