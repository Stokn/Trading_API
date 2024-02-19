package com.vulturi.trading.api.services.kyc;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserKycStatus;
import com.vulturi.trading.api.models.user.UserStartKyc;

import java.io.IOException;

public interface KycService {

    UserStartKyc initiate(User user) throws IOException;
    void updateUserKycStatus(String userId, UserKycStatus userKycStatus) throws ApiException;


}
