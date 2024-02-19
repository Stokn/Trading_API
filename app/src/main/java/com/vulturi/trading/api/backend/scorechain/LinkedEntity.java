package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter@Setter@JsonIgnoreProperties(ignoreUnknown = true)
public class LinkedEntity {
    private Long id;
    private String name;
    private Collection<String> countries;
    private String type;
}
