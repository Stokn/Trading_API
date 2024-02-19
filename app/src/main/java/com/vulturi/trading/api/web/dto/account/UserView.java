package com.vulturi.trading.api.web.dto.account;


import lombok.Data;

import java.util.Collection;

@Data
public class UserView {
    private String userId;
    private Collection<String> accountId;



}
