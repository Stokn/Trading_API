package com.vulturi.trading.api.web.dto.dca;

import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.dca.DCAFrequency;
import com.vulturi.trading.api.models.dca.DCAStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class DCAView {
    private String accountId;
    private String id;
    private LocalDateTime creationTs;
    private DCAStatus status;
    private BigDecimal quantity;
    private Coin fromCoin;
    private Coin toCoin;
    private DCAFrequency frequency;
}
