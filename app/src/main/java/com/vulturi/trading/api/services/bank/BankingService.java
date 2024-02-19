package com.vulturi.trading.api.services.bank;

import com.vulturi.trading.api.models.deposit.BankInfo;
import org.springframework.stereotype.Service;

public interface BankingService {
    BankInfo save(BankInfo bankInfo);

    BankInfo get(String iban);

    BankInfo getBankForDeposit();


}
