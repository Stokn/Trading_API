package com.vulturi.trading.api.models.user;

import lombok.Data;

@Data
public class GetRefreshToken {
    private String username;
    private String refreshToken;
}
