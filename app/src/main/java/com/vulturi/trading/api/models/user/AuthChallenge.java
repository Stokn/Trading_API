package com.vulturi.trading.api.models.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data@Builder@AllArgsConstructor
@NoArgsConstructor
public class AuthChallenge {
    private String sessionId;
    private String username;
    private String challengeType;
}
