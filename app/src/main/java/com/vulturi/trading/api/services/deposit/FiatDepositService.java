package com.vulturi.trading.api.services.deposit;

import com.vulturi.trading.api.models.deposit.UserBankDetails;
import com.vulturi.trading.api.models.user.User;

public interface FiatDepositService {
    UserBankDetails get(User user);

}
