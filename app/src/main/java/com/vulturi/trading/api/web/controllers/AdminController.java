package com.vulturi.trading.api.web.controllers;

import com.google.common.collect.Lists;
import com.vulturi.exchanges.connector.model.ExchangeCredentials;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.coin.GetCoinCreationRequest;
import com.vulturi.trading.api.models.deposit.BankInfo;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserKycStatus;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import com.vulturi.trading.api.services.bank.BankingService;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.services.user.UserService;
import com.vulturi.trading.api.util.ExchangeCredentialsHandler;
import com.vulturi.trading.api.web.dto.TransactionView;
import com.vulturi.trading.api.web.factory.TransactionFactory;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

@RestController
@RequestMapping("/v1/admin")
public class AdminController {
    @Autowired
    private PortfolioManagerService portfolioManagerService;

    @Autowired
    private ExchangeCredentialsHandler exchangeCredentialsHandler;

    @Autowired
    private CoinService coinService;

    @Autowired
    private UserService userService;

    @Autowired
    private BankingService bankingService;

    @Operation(summary = "Create available coins for deposit",hidden = true)
    @PostMapping("deposit/available-coins")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Coin>> availableCoins(@RequestBody GetCoinCreationRequest getCoinCreationRequest) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(coinService.create(getCoinCreationRequest)));
    }

    @Operation(summary = "Create available coins for deposit",hidden = true)
    @PostMapping("banking/bank/add")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<BankInfo>> addBank(@RequestBody BankInfo bankInfo) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(bankingService.save(bankInfo)));
    }

    @Operation(summary = "Update available coins for deposit", hidden = true)
    @PutMapping("deposit/update-available-coin")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Coin>> updateCoin(@RequestBody Coin coin) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(coinService.update(coin)));
    }


    @Operation(summary = "Update available coins for deposit", hidden = true)
    @PutMapping("/coins/reset-creation-ts")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Collection<Coin>>> refreshCoin() throws ApiException {
        ArrayList<Coin> coins = new ArrayList<>();
        for (Coin coin : coinService.findAll()) {
            coin.setCreationTs(LocalDateTime.now(Clock.systemUTC()));
            coins.add(coinService.update(coin)) ;
        }
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(coins));
    }


    @Operation(summary = "Update UserKycStatus",hidden = true)
    @PutMapping("deposit/update-kyc-status/{userId}/{userKycStatus}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<User>> updateKycStatus(@PathVariable("userId") String userId , @PathVariable("userKycStatus") UserKycStatus userKycStatus) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userService.updateKycStatus(userId,userKycStatus)));
    }


    @PostMapping("/setup/encryption/{encryptionKey}/{exchange}")
    @Operation(hidden = true)
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<Boolean> setExchangeCredentialsEncryptionKey(@PathVariable("encryptionKey") String encryptionKey, @PathVariable("exchange") ExchangePlatform exchange, @RequestBody ExchangeCredentials exchangeCredentials) {
        switch (exchange) {
            case BINANCE -> {
                return ResponseEntity.ok(exchangeCredentialsHandler.setBinanceBrokerageCredentials(encryptionKey, exchangeCredentials));
            }
            case KRAKEN -> {
                return ResponseEntity.ok(exchangeCredentialsHandler.setKrakenCredentials(encryptionKey, exchangeCredentials));
            }
            default -> {
                return ResponseEntity.ok(false);
            }
        }
    }

    @GetMapping("/key/{exchange}")
    @Operation(hidden = true)
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<ExchangeCredentials>> setExchangeCredentials(@PathVariable("exchange") ExchangePlatform exchange) {
        switch (exchange) {
            case BINANCE -> {
                return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(exchangeCredentialsHandler.getDecryptedCredentialsForBrokerageAccount()));
            }
            case KRAKEN -> {
                return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(exchangeCredentialsHandler.getDecryptedCredentialsForKrakenAccount()));
            }
            default -> {
                return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(ExchangeCredentials.builder().build()));
            }
        }
    }
    @Operation(hidden = true)
    @PostMapping("/transaction")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Transaction>> addTransaction(@RequestBody TransactionView transactionView) {
        Transaction transaction = TransactionFactory.toTransaction(transactionView);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(portfolioManagerService.saveOrUpdate(transaction)));
    }
    @Operation(hidden = true)
    @DeleteMapping("/transaction/{id}")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/admin.Write')")
    public ResponseEntity<ApiResponse<Boolean>> deleteTransaction(@PathVariable("id") String id) {
        portfolioManagerService.delete(id);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(true));
    }
}
