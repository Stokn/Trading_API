package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data@JsonIgnoreProperties(ignoreUnknown = true)
public class Identification {
    private String type;
    private String id;
    private Attributes attributes;
    private Relationships relationships;
}