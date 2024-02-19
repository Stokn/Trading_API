package com.vulturi.trading.api.web.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class TradingOrderView {
    private String accountId;
    private String from;
    private String to;
    private BigDecimal quantity;
}
