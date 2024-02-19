package com.vulturi.trading.api;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.account.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;

@SpringBootTest
@ActiveProfiles("test")
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;
    private final static String accountId = "test-accountId";
    private final static String email = "test@stokn.io";

    private void before() throws ApiException{
        accountService.create(accountId, email);

    }
    @Test
    public void get() throws ApiException{
        before();
        Account account = accountService.get(accountId);
        Portfolio portfolio = new Portfolio();
        portfolio.setAccountId(accountId);
        portfolio.setPortfolioAccountType(PortfolioAccountType.TRADING);
        account.setPortfolios(Collections.singleton(portfolio));
        accountService.save(account);

    }
}
