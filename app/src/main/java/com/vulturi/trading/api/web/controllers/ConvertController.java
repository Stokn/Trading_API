package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.coin.Coin;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.exchanges.MultiExchangeTradingService;
import com.vulturi.trading.api.services.marketplace.ConvertService;
import com.vulturi.trading.api.services.user.UserService;
import com.vulturi.trading.api.web.dto.ConvertView;
import com.vulturi.trading.api.web.dto.TradingOrderView;
import com.vulturi.trading.api.web.factory.ConvertFactory;
import com.vulturi.trading.api.web.factory.TradingOrderFactory;
import com.vulturi.trading.api.web.factory.TradingTransactionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
@RequestMapping("/v1/convert")
public class ConvertController {


    @Autowired
    private MultiExchangeTradingService multiExchangeTradingService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TradingTransactionFactory tradingTransactionFactory;

    @Autowired
    private ConvertFactory convertFactory;

    @Autowired
    private ConvertService convertService;
    @CrossOrigin(origins = {"*"})
    @PostMapping(value = "")
    public ResponseEntity<ApiResponse<Collection<ConvertView>>> trade(@AuthenticationPrincipal Jwt jwt, @RequestBody @Validated TradingOrderView tradingOrderView) throws ApiException {
        User me = userService.me(jwt);
        Account account = me.getAccountIds().stream().map(s -> accountService.get(s)).findFirst().orElse(null);
        if(account!=null){
            Collection<TradingTransaction> data = multiExchangeTradingService.placeOrder(account, TradingOrderFactory.tradingOrder(tradingOrderView));
            return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(data.stream().map(convertFactory::toConvertView).toList()));
        }
        return ResponseEntity.ok(ApiResponse.buildFailedApiResult(404,"Cannot find account for trading order"));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping(value = "/{fromSlug}")
    public ResponseEntity<ApiResponse<Collection<Coin>>> trade(@PathVariable("fromSlug") String fromCoinSlug) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(convertService.getAvailableCoinForTrading(fromCoinSlug)));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping(value = "/tradingPairs")
    public ResponseEntity<ApiResponse<Collection<String>>> pairs() throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(convertService.getAvailablePairsForTrading()));
    }


}
