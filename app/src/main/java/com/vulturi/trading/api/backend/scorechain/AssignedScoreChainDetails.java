package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@JsonIgnoreProperties(ignoreUnknown = true)
public class AssignedScoreChainDetails {
    private LinkedEntity entity;
}
