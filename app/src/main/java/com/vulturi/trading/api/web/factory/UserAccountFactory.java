package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.models.user.User;
import com.vulturi.trading.api.models.user.UserKycStatus;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.web.dto.account.UserAccountView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserAccountFactory {
    @Autowired
    private AccountService accountService;

    public UserAccountView toAccountView(User user){
        List<Account> accounts = user.getAccountIds().stream().map(s -> accountService.get(s)).toList();
        UserAccountView view = new UserAccountView();
        view.setKycIds(user.getKycIds());
        view.setUserAccountStatus(user.getKycStatus()==null? UserKycStatus.UNINITIATED.toString():user.getKycStatus().toString());
        view.setDocumentIds(user.getDocumentIds());
        view.setUserId(user.getUserId());
        view.setAccounts(accounts);
        view.setEmail(user.getEmail());
        view.setPhoneNumber(user.getPhoneNumber());
        view.setInfo(user.getInfo());
        view.setAddress(user.getAddress());
        view.setBankingTransferRefId(user.getBankingTransferRefId());
        return view;
    }


}
