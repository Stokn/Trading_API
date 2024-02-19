package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter@Setter@JsonIgnoreProperties(ignoreUnknown = true)
public class ScoreChainAnalysis {
    private AssignedScore assigned;
    private DynamicScore incoming;
    private DynamicScore outgoing;
}
