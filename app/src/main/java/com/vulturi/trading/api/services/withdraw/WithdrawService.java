package com.vulturi.trading.api.services.withdraw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.withdraw.WithdrawRequest;
import com.vulturi.trading.api.models.withdraw.WithdrawAddressCreationRequest;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;

public interface WithdrawService {

    void addAddress(WithdrawAddressCreationRequest withdrawAddressCreationRequest) throws JsonProcessingException, ApiException;

    WithdrawResponse withdraw(WithdrawRequest withdrawRequest) throws ApiException, JsonProcessingException;




}
