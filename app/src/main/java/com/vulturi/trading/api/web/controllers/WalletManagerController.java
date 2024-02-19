package com.vulturi.trading.api.web.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vulturi.exchanges.connector.model.Deposit;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.withdraw.WithdrawRequest;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionFilter;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.services.deposit.DepositService;
import com.vulturi.trading.api.services.user.UserService;
import com.vulturi.trading.api.services.withdraw.WithdrawService;
import com.vulturi.trading.api.web.dto.TransactionView;
import com.vulturi.trading.api.web.dto.deposit.DepositAddressView;
import com.vulturi.trading.api.web.factory.DepositAddressFactory;
import com.vulturi.trading.api.web.factory.TransactionFactory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/v1/wallet")
public class WalletManagerController {

    @Autowired
    private UserService userService;
    @Autowired
    private PortfolioManagerService portfolioManagerService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private WithdrawService withdrawService;

    @Autowired
    private DepositService depositService;
    @CrossOrigin(origins = {"*"})
    @GetMapping("transactions/{accountId}")
    public ResponseEntity<ApiResponse<Collection<Transaction>>> get(@PathVariable("accountId") String accountId) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(portfolioManagerService.get(accountId)));
    }
    @CrossOrigin(origins = {"*"})
    @PostMapping("transactions/filter")
    public ResponseEntity<Collection<Transaction>> filter(@RequestBody TransactionFilter filter) throws ApiException {
        return ResponseEntity.ok(portfolioManagerService.filter(filter));
    }

    @Operation(summary = "Get deposit on chain address")
    @Parameter(name = "jwt", hidden = true)
    @GetMapping("deposit/{accountId}/{asset}/{network}")
    public ResponseEntity<ApiResponse<DepositAddressView>> deposit(@PathVariable("accountId") String accountId, @PathVariable("asset") String asset, @PathVariable("network") String network) throws ApiException {
        DepositAddressView data = DepositAddressFactory.toView(depositService.findByAssetAndBlockchain(accountId, asset, network));
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(data));
    }
    @Operation(summary = "Get deposit history on chain")
    @Parameter(name = "jwt", hidden = true)
    @GetMapping("deposit/{accountId}/history")
    public ResponseEntity<ApiResponse<Collection<Deposit>>> depositHistory(@PathVariable("accountId") String accountId) throws ApiException {
        Collection<Deposit> data = depositService.getHistoricalDeposit(accountId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(data));
    }

    @Operation(summary = "Get available network for deposit")
    @GetMapping("deposit/networks/available/{slug}")
    public ResponseEntity deposit(@PathVariable("slug") String slug) throws ApiException {
        return ResponseEntity.ok(coinService.findBySlug(slug).getAvailableNetworks());
    }

    @Operation(summary = "Get network of coins")
    @GetMapping("deposit/networks/all/{slug}")
    public ResponseEntity depositNetworks(@PathVariable("slug") String slug) throws ApiException {
        return ResponseEntity.ok(depositService.findNetworkForAsset(slug));
    }


    @Operation(summary = "Get available coins for deposit")
    @GetMapping("deposit/available-coins")
    public ResponseEntity<ApiResponse<Collection<Coin>>> availableCoins() throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(coinService.findAll()));
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("withdraw")
    public ResponseEntity<ApiResponse<WithdrawResponse>> withdraw(@RequestBody WithdrawRequest withdrawRequest) throws ApiException, JsonProcessingException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(withdrawService.withdraw(withdrawRequest)));
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("/transaction")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Transaction>> addTransaction(@RequestBody TransactionView transactionView) {
        Transaction transaction = TransactionFactory.toTransaction(transactionView);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(portfolioManagerService.saveOrUpdate(transaction)));
    }

    
    @DeleteMapping("/transaction/{id}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Boolean>> addTransaction(@PathVariable("id") String id) {
        portfolioManagerService.delete(id);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(true));
    }



}
