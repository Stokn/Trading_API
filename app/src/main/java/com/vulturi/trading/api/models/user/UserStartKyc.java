package com.vulturi.trading.api.models.user;

import lombok.Data;
import lombok.Getter;

@Data
public class UserStartKyc {
    private String identificationUrl;
    private String userId;
    private String phoneNumber;
    private int numberOfAttempts;
    private String  identificationId;
}
