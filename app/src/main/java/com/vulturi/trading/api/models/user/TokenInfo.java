package com.vulturi.trading.api.models.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

@Data
@Builder
public class TokenInfo implements Serializable {
    private String username;
    private LocalDateTime issuedAt;
    private String accessToken;
    private String idToken;
    private String refreshToken;
    private Integer expiresIn;
    private LocalDateTime expiresAt;
    private String phoneNumber;
    public LocalDateTime getExpiresAt() {
        return issuedAt.plusSeconds(this.expiresIn).minusMinutes(10);
    }
}