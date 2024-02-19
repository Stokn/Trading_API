package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data@JsonIgnoreProperties(ignoreUnknown = true)
public class RelationshipData {
    private DataElement[] data;
    private Links links;
    private Meta meta;
}