package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data@JsonIgnoreProperties(ignoreUnknown = true)
public class Relationships {
    @JsonProperty("doc-face-matches")
    private RelationshipData docFaceMatches;
    @JsonProperty("doc-doc-matches")
    private RelationshipData docDocMatches;
    @JsonProperty("reason-code")
    private RelationshipData reasonCodes;
    @JsonProperty("document-checks")
    private RelationshipData documentChecks;
    @JsonProperty("face-checks")
    private RelationshipData faceChecks;
    @JsonProperty("identity-form-match")
    private RelationshipData identityFormMatch;
    private SingleRelationshipData identity;
}