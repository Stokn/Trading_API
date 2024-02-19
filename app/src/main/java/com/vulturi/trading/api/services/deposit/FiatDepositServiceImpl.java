package com.vulturi.trading.api.services.deposit;

import com.vulturi.trading.api.models.deposit.BankInfo;
import com.vulturi.trading.api.models.deposit.UserBankDetails;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.services.bank.BankingService;
import com.vulturi.trading.api.services.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FiatDepositServiceImpl implements FiatDepositService {
    @Autowired
    private BankingService bankingService;


    @Override
    public UserBankDetails get(User user) {
        BankInfo bankForDeposit = bankingService.getBankForDeposit();
        UserBankDetails userBankDetails = new UserBankDetails();
        userBankDetails.setBankInfo(bankForDeposit);
        userBankDetails.setUserId(user.getUserId());
        userBankDetails.setBankingTransferRefId(user.getBankingTransferRefId());
        return userBankDetails;
    }
}
