package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data@JsonIgnoreProperties(ignoreUnknown = true)
public class Links {
    private String related;
}