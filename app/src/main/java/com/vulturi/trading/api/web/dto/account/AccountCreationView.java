package com.vulturi.trading.api.web.dto.account;

import lombok.Data;

@Data
public class AccountCreationView {
    private String userId;
    private String accountId;
    private String email;
}
