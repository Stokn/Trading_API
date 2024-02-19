package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.LinkedHashMap;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class UbbleIdentificationResponse {
    private Identification data;
    private Document document;
}
