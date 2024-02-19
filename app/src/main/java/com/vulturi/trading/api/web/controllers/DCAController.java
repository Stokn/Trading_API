package com.vulturi.trading.api.web.controllers;

import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.ApiResponse;
import com.vulturi.trading.api.models.dca.DCA;
import com.vulturi.trading.api.models.dca.DCAStatus;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.dca.DCAService;
import com.vulturi.trading.api.services.user.UserService;
import com.vulturi.trading.api.web.dto.dca.DCACreateView;
import com.vulturi.trading.api.web.dto.dca.DCAView;
import com.vulturi.trading.api.web.factory.DCAFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/dca")
public class DCAController {
    @Autowired
    private DCAFactory dcaFactory;
    @Autowired
    private DCAService dcaService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @CrossOrigin(origins = {"*"})
    @GetMapping(value = "")
    public ResponseEntity<ApiResponse<List<DCAView>>> getActiveDCA(@AuthenticationPrincipal Jwt jwt) throws ApiException {
        User me = userService.me(jwt);
        Account account = me.getAccountIds().stream().map(s -> accountService.get(s)).findFirst().orElse(null);
        if(account!=null){
            List<DCA> activeDCA = dcaService.getDCA(account);
            return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(activeDCA.stream().map(dcaFactory::toView).toList()));
        }
        return ResponseEntity.ok(ApiResponse.buildFailedApiResult(404,"Cannot find account for trading order"));
    }
    @CrossOrigin(origins = {"*"})
    @PostMapping(value = "")
    public ResponseEntity<ApiResponse<DCAView>> create(@AuthenticationPrincipal Jwt jwt, @RequestBody DCACreateView view) throws ApiException {
        User me = userService.me(jwt);
        Account account = me.getAccountIds().stream().map(s -> accountService.get(s)).findFirst().orElse(null);
        if(account!=null){
            DCA dca = dcaService.save(dcaFactory.toDCA(account, view));
            return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(dcaFactory.toView(dca)));
        }
        return ResponseEntity.ok(ApiResponse.buildFailedApiResult(404,"Cannot find account for trading order"));
    }
    @CrossOrigin(origins = {"*"})
    @PutMapping(value = "/{id}/{status}")
    public ResponseEntity<ApiResponse<DCAView>> create(@AuthenticationPrincipal Jwt jwt, @PathVariable("id")String id, @PathVariable("status")DCAStatus status) throws ApiException {
        User me = userService.me(jwt);
        Account account = me.getAccountIds().stream().map(s -> accountService.get(s)).findFirst().orElse(null);
        if(account!=null){
            DCA update = dcaService.update(account, id, status);
            return ResponseEntity.ok(ApiResponse.buildSuccessfulApiResult(dcaFactory.toView(update)));
        }
        return ResponseEntity.ok(ApiResponse.buildFailedApiResult(404,"Cannot find account for trading order"));
    }

}
