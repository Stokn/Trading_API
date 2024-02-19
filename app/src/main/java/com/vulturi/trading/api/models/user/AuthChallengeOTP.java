package com.vulturi.trading.api.models.user;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter@Setter@ToString
public class AuthChallengeOTP extends  AuthChallenge{
    private String code;

}
