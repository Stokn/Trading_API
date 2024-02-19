package com.vulturi.trading.api.backend.ubble;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Attributes {
    private String comment;
    @JsonProperty("create-at")
    private LocalDateTime createdAt;
    @JsonProperty("ended-at")
    private LocalDateTime endedAt;
    @JsonProperty("identification-id")
    private String identificationId;
    @JsonProperty("identification-url")
    private String identificationUrl;
    @JsonProperty("number-of-attempts")
    private int numberOfAttempts;
    @JsonProperty("redirect-url")
    private String redirectUrl;
    private double score;
    @JsonProperty("started-at")
    private LocalDateTime startedAt;
    private String status;
    @JsonProperty("updated-at")
    private LocalDateTime updatedAt;
    @JsonProperty("status-updated-at")
    private LocalDateTime statusUpdatedAt;
    @JsonProperty("user-agent")
    private String userAgent;
    @JsonProperty("user-ip-address")
    private String userIpAddress;
    private String webhook;
}