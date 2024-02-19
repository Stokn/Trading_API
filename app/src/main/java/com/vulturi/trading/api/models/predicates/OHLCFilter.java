package com.vulturi.trading.api.models.predicates;

import com.vulturi.trading.api.models.enums.OHLCFrequency;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.Collection;
@Data
public class OHLCFilter {
    private Collection<String> products;
    private OHLCFrequency frequency;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime minTs;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime maxTs;
    private Boolean lastOnly;
}
