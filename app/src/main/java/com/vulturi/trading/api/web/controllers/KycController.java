package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.backend.ubble.UbbleService;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserDocument;
import com.vulturi.trading.api.models.user.UserKycStatus;
import com.vulturi.trading.api.models.user.UserStartKyc;
import com.vulturi.trading.api.services.kyc.KycService;
import com.vulturi.trading.api.services.kyc.UserDocumentService;
import com.vulturi.trading.api.services.user.UserService;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@RestController
@RequestMapping("/v1/kyc")
public class KycController {

    @Autowired
    private UserDocumentService userDocumentService;

    @Autowired
    private UbbleService ubbleService;

    @Autowired
    private UserService userService;

    @Autowired
    private KycService kycService;
    @CrossOrigin(origins = {"*"})
    @GetMapping("/documents/{userId}")
    public ResponseEntity<ApiResponse<Collection<UserDocument>>> getAll(@PathVariable("userId") String userId) {
        log.info("Requesting KYC documents for userId: {}", userId);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(userDocumentService.get(userId)));
    }
    @CrossOrigin(origins = {"*"})
    @Parameter(name = "jwt", hidden = true)
    @GetMapping("/start")
    public ResponseEntity<ApiResponse<UserStartKyc>> start(@AuthenticationPrincipal Jwt jwt) throws ApiException, IOException {
        User me = userService.me(jwt);
        log.info("Start KYC for userId: {}", me.getUserId());
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(kycService.initiate(me)));
    }
    @CrossOrigin(origins = {"*"})
    @GetMapping("/update-kyc-status/{userId}/{kycStatus}")
    public ResponseEntity<ApiResponse<Boolean>> updateStatus(@PathVariable("userId") String userId, @PathVariable("kycStatus") UserKycStatus userKycStatus) throws ApiException, IOException {
        log.info("Updating KYC Status for userId: {}", userId);
        kycService.updateUserKycStatus(userId,userKycStatus);
        return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(true));
    }
}
