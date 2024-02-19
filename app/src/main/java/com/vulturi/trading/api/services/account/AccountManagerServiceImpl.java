package com.vulturi.trading.api.services.account;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.AccountManagerDao;
import com.vulturi.trading.api.models.user.AccountManager;
import org.hibernate.annotations.LazyCollectionOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AccountManagerServiceImpl implements AccountManagerService {

    @Autowired
    private AccountManagerDao dao;


    private Map<String, AccountManager> cache = Maps.newConcurrentMap();


}
