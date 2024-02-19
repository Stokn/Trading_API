package com.vulturi.trading.api.models.user;

import lombok.Data;

@Data
public class UserAttributesUpdate {
    private String lastname;
    private String name;
    private String nationality;
    private String address;
}
