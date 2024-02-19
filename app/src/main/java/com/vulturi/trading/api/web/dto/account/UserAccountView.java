package com.vulturi.trading.api.web.dto.account;

import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.user.UserKycStatus;
import lombok.Data;
import software.amazon.awssdk.services.sns.endpoints.internal.Value;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Data
public class UserAccountView {
    private String userId;
    private String phoneNumber;
    private String name;
    private String surname;
    private Collection<Account> accounts;
    private Collection<String> kycIds;
    private Collection<String> documentIds = new ArrayList<>();
    private Map<String,String> address;
    private Map<String,String> info;
    private String email;
    private String userAccountStatus;
    private String bankingTransferRefId;
}
