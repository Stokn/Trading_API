package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
@Data
public class Document {
    @JsonProperty("all-first-names")
    private List<String> allFirstNames;
    @JsonProperty("birth-date")
    private LocalDate birthDate;
    @JsonProperty("document-number")
    private String documentNumber;
    @JsonProperty("document-type")
    private String documentType;
    @JsonProperty("expiry-date")
    private LocalDate expiryDate;
    @JsonProperty("first-name")
    private String firstName;
    @JsonProperty("last-name")
    private String lastName;
    private String gender;
    @JsonProperty("issue-date")
    private LocalDate issueDate;
    @JsonProperty("married-name")
    private String marriedName;
    private String nationality;
}
