package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.balance.AccountBalance;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.portfolio.PortfolioCreationRequest;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.portfolio.PortfolioService;
import com.vulturi.trading.api.web.dto.account.AccountCreationView;
import com.vulturi.trading.api.web.dto.account.UserAccountView;
import com.vulturi.trading.api.web.dto.balance.GlobalBalanceView;
import com.vulturi.trading.api.web.factory.GlobalBalanceFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/account")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private PortfolioService portfolioService;

    @Autowired
    private GlobalBalanceFactory globalBalanceFactory;
    @CrossOrigin(origins = {"*"})
    @GetMapping("activate/{accountId}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Account>> activate(@PathVariable("accountId") String accountId) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(accountService.activate(accountId)));
    }
    @CrossOrigin(origins = {"*"})
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Account>> create(@RequestBody @Validated AccountCreationView view) throws ApiException {
        Account body = accountService.create(view.getAccountId(), view.getEmail());
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(body));
    }
    @CrossOrigin(origins = {"*"})
    @PostMapping("/portfolio")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Account>> createPortfolio(@RequestBody @Validated PortfolioCreationRequest request) throws ApiException {
        Account body = accountService.createPortfolio(request);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(body));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/portfolios/{accountId}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/account.Read')")
    public ResponseEntity<ApiResponse<Collection<Portfolio>>> get(@PathVariable("accountId") String accountId) throws ApiException {
        Collection<Portfolio> byAccountId = portfolioService.getByAccountId(accountId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(byAccountId));
    }





    @CrossOrigin(origins = {"*"})

    @GetMapping("portfolio/{accountId}/{portfolioId}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/account.Read')")
    public ResponseEntity<ApiResponse<Portfolio>> getByAccountIdAndPortfolioId(@PathVariable("accountId") String accountId,@PathVariable("portfolioId") String portfolioId) throws ApiException {
        Portfolio portfolio = portfolioService.getByAccountIdAndPortfolioId(accountId,portfolioId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(portfolio));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/{accountId}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/account.Read')")
    public ResponseEntity<ApiResponse<Account>> getByAccountIdAndPortfolioId(@PathVariable("accountId") String accountId) throws ApiException {
        Account body = accountService.get(accountId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(body));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/{accountId}/balance")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/account.Read')")
    public ResponseEntity<ApiResponse<GlobalBalanceView>> getGlobalBalance(@PathVariable("accountId") String accountId) throws ApiException {
        GlobalBalance globalBalance = accountService.getGlobalBalance(accountId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(globalBalanceFactory.toGlobalBalanceView(globalBalance)));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/{accountId}/historical/balance")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/account.Read')")
    public ResponseEntity<ApiResponse<Collection<AccountBalance>>> getHistoricalGlobalBalance(@PathVariable("accountId") String accountId) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(accountService.getHistoricalGlobalBalance(accountId)));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/compute/historical/balance")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Boolean>> createPortfolio() throws ApiException {
        accountService.computeGlobalBalances(LocalDateTime.now(Clock.systemUTC()).minusDays(20).truncatedTo(ChronoUnit.HOURS));
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(true));
    }

    @CrossOrigin(origins = {"*"})
    @DeleteMapping("/{accountId}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/account.Read')")
    public ResponseEntity<ApiResponse<Boolean>> delete(@PathVariable("accountId") String accountId) throws ApiException {
        accountService.delete(accountId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(true));
    }






}
