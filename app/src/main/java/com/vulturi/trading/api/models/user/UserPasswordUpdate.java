package com.vulturi.trading.api.models.user;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = true)
public class UserPasswordUpdate extends AuthChallenge {
    private String password;
    private String passwordConfirm;
}
