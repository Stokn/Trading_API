package com.vulturi.trading.api.models.balance;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AccountBalance {
    private String accountId;
    private BigDecimal amountEUR;
    private LocalDateTime ts;
}
