package com.vulturi.trading.api;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionType;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.balance.GlobalBalanceService;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.web.dto.balance.GlobalBalanceView;
import com.vulturi.trading.api.web.factory.GlobalBalanceFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.IntStream;

@SpringBootTest
@ActiveProfiles("test")
public class GlobalBalanceServiceTest {

    @Autowired
    private PortfolioManagerService portfolioManagerService;

    @Autowired
    private GlobalBalanceFactory globalBalanceFactory;

    @Autowired
    private AccountService accountService;

    @Autowired
    private GlobalBalanceService globalBalanceService;

    @Autowired
    private CoinService coinService;

    private final static String accountId = "test-accountId";
    private final static String email = "test@stokn.io";

    private void before() throws ApiException, InterruptedException {
        accountService.create(accountId, email);
        Account account = accountService.get(accountId);
        Portfolio portfolio = new Portfolio();
        portfolio.setName("MAIN");
        portfolio.setAccountId(accountId);
        portfolio.setPortfolioAccountType(PortfolioAccountType.TRADING);
        account.setPortfolios(Collections.singleton(portfolio));
        accountService.save(account);


    }
    private Collection<Transaction> generateTransactionRandom(){
        Collection<Transaction> transactions = new ArrayList<>();
        Account account =accountService.get(accountId);

        List<String> assetList = Arrays.asList("BTC", "ETH");

        for (int number = 0; number < 10000; number++) {

            TransactionSide transactionSide = TransactionSide.CREDIT;
            TransactionType transactionType = TransactionType.DEPOSIT;
            String asset = assetList.get(1);
            if((number % 2) == 0){

                asset = assetList.get(0);
            }

            Transaction transaction = new Transaction();
            transaction.setTransactionSide(transactionSide);
            transaction.setTransactionType(transactionType);
            transaction.setAccountId(account.getAccountId());
            transaction.setPortfolioId(Objects.requireNonNull(account.getPortfolios().stream().filter(portfolio -> portfolio.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findAny().orElse(null)).getPortfolioId());
            transaction.setAsset(asset);
            transaction.setAmount(BigDecimal.valueOf(15));
            transactions.add(transaction);
        }
        return transactions;
    }

    private Collection<Transaction> generateTransactions(){
        Collection<Transaction> transactions = new ArrayList<>();
        Account account =accountService.get(accountId);

        IntStream.range(0, 10000).forEach(number -> {
            Transaction transaction = new Transaction();
            transaction.setTransactionSide(TransactionSide.CREDIT);
            transaction.setTransactionType(TransactionType.DEPOSIT);
            transaction.setAccountId(account.getAccountId());
            transaction.setPortfolioId(Objects.requireNonNull(account.getPortfolios().stream().filter(portfolio -> portfolio.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findAny().orElse(null)).getPortfolioId());
            transaction.setAsset("USDT");
            transaction.setAmount(BigDecimal.valueOf(15));
            transactions.add(transaction);
        });
       return transactions;
    }

    @Test
    public void get() throws ApiException, InterruptedException {
        before();
        portfolioManagerService.saveAll(generateTransactions());
        portfolioManagerService.saveAll(generateTransactionRandom());
        Account account =accountService.get(accountId);
        WithdrawResponse withdrawResponse = new WithdrawResponse();
        withdrawResponse.setAsset("USDT");
        withdrawResponse.setNetwork("ETH");
        withdrawResponse.setPortfolioId(Objects.requireNonNull(account.getPortfolios().stream().filter(portfolio -> portfolio.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findAny().orElse(null)).getPortfolioId());
        withdrawResponse.setAccountId(account.getAccountId());
        withdrawResponse.setAmount(BigDecimal.valueOf(100));
        portfolioManagerService.register(withdrawResponse);
        GlobalBalance globalBalance = globalBalanceService.get(account, LocalDateTime.now());
        //GlobalBalanceView globalBalanceView = globalBalanceFactory.toGlobalBalanceView(globalBalance);
    }
}
