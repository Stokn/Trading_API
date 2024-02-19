package com.vulturi.trading.api.web.dto.marketplace;

import com.vulturi.trading.api.models.enums.MarketPlaceCategory;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
@Data
public class MarketPlaceProductView {
    private MarketPlaceCategory category;
    private String coin;
    private String coinName;
    private String coinLogoUrl;
    private Map<String, BigDecimal> pricesPerCcy;
    private Map<String, BigDecimal> closePrices;
}
