package com.vulturi.trading.api.web.controllers;

import com.amazonaws.services.cognitoidp.model.ForgotPasswordResult;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.balance.PortfolioBalance;
import com.vulturi.trading.api.models.deposit.UserBankDetails;
import com.vulturi.trading.api.models.user.*;
import com.vulturi.trading.api.services.deposit.FiatDepositService;
import com.vulturi.trading.api.services.user.UserService;
import com.vulturi.trading.api.web.dto.account.UserAccountView;
import com.vulturi.trading.api.web.dto.balance.PortfolioBalanceView;
import com.vulturi.trading.api.web.factory.GlobalBalanceFactory;
import com.vulturi.trading.api.web.factory.UserAccountFactory;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.util.Collection;

@RestController
@Slf4j
@RequestMapping("v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserAccountFactory userAccountFactory;
    @Autowired
    private FiatDepositService fiatDepositService;
    @Autowired
    private GlobalBalanceFactory globalBalanceFactory;

    @CrossOrigin(origins = {"*"})
    @PostMapping(value = "/sign-up")
    @PreAuthorize("hasAuthority('SCOPE_stokn-api/user.Write')")
    public ResponseEntity<ApiResponse<UserAccountView>> signUp(@RequestBody @Validated UserSignUp userSignUp) throws ApiException {
        User user = userService.createUser(userSignUp);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userAccountFactory.toAccountView(user)));
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody @Validated UserLogin userLogin) throws ApiException {
        AuthResponse<?> authenticate = userService.authenticate(userLogin);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(authenticate.getData()));
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("/otp")
    public ResponseEntity<ApiResponse<?>> otp(@RequestBody @Validated AuthChallengeOTP authChallengeOTP) throws ApiException {
        AuthResponse<?> body = userService.responseToAuthChallenge(authChallengeOTP);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(body.getData()));
    }

    @CrossOrigin(origins = {"*"})
    @Parameter(name = "jwt", hidden = true)
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserAccountView>> me(@AuthenticationPrincipal Jwt jwt) throws ApiException {
        User me = userService.me(jwt);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userAccountFactory.toAccountView(me)));
    }

    @CrossOrigin(origins = {"*"})
    @Parameter(name = "jwt", hidden = true)
    @GetMapping("/bank/details")
    public ResponseEntity<ApiResponse<UserBankDetails>> getUserBankDetails(@AuthenticationPrincipal Jwt jwt) throws ApiException {
        User me = userService.me(jwt);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(fiatDepositService.get(me)));
    }


    @CrossOrigin(origins = {"*"})
    @Parameter(name = "jwt", hidden = true)
    @GetMapping("/portfolio-balance")
    public ResponseEntity<ApiResponse<Collection<PortfolioBalanceView>>> portfolio(@AuthenticationPrincipal Jwt jwt) throws ApiException {
        Collection<PortfolioBalance> portfolioBalances = userService.myPortfolioBalance(jwt);
        log.info("Portfolio balances of user {}", portfolioBalances);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(portfolioBalances.stream().map(globalBalanceFactory::toPortfolioBalanceView).toList()));
    }

    @CrossOrigin(origins = {"*"})
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody GetRefreshToken getRefreshToken) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userService.refresh(getRefreshToken).getData()));
    }

    @CrossOrigin(origins = {"*"})
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<AuthResponse<?>>> changePassword(@RequestBody @Validated UserPasswordUpdate userPasswordUpdate) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userService.updateUserPassword(userPasswordUpdate)));
    }

    @CrossOrigin(origins = {"*"})
    @PutMapping("/update-info")
    public ResponseEntity<ApiResponse<AuthResponse<?>>> updateInfo(@RequestBody @Validated UserPasswordUpdate userPasswordUpdate) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userService.updateUserPassword(userPasswordUpdate)));
    }

    @CrossOrigin(origins = {"*"})
    @GetMapping(value = "/forget-password")
    public ResponseEntity<ApiResponse<ForgotPasswordResult>> forgotPassword(@NotNull @NotEmpty @Email @RequestParam("email") String email) throws ApiException {
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userService.userForgotPassword(email)));
    }

}