package com.vulturi.trading.api.models.user;

import lombok.Data;
import lombok.ToString;

import java.util.Set;

@Data@ToString
public class UserSignUp {
    // 1st step auth
    private String phoneNumber;
    private String email;
    private String password;
    // check OTP Sms
    // check OTP Email
    // 2nd step

    // 3rd personal questionnaire
    private Set<String> roles; // todo voir si on le garde
}
