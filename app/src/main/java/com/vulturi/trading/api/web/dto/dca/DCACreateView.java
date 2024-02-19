package com.vulturi.trading.api.web.dto.dca;

import com.vulturi.trading.api.models.dca.DCAFrequency;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DCACreateView {
    private BigDecimal quantity;
    private String from;
    private String to;
    private DCAFrequency frequency;
}
