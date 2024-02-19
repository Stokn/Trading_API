package com.vulturi.trading.api.web.dto;

import com.vulturi.trading.api.models.transaction.Transaction;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class ConvertView {
    private String accountId;
    private String portfolioId;
    private Transaction from;
    private Transaction to;
    private Transaction fees;
}
