package com.vulturi.trading.api.services.bank;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.BankInfoDao;
import com.vulturi.trading.api.models.deposit.BankInfo;
import com.vulturi.trading.api.models.deposit.BankName;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BankingServiceImpl implements BankingService {
    @Autowired
    private BankInfoDao dao;
    private Map<String,BankInfo> cache = Maps.newConcurrentMap();

    @PostConstruct
    void init(){
        cache = dao.findAll().stream().collect(Collectors.toMap(BankInfo::getIban, bI->bI));
    }

    @Override
    public BankInfo save(BankInfo bankInfo) {
        dao.save(bankInfo);
        cache.put(bankInfo.getIban(),bankInfo);
        return cache.get(bankInfo.getIban());
    }

    @Override
    public BankInfo get(String iban) {
        return cache.get(iban);
    }

    @Override
    public BankInfo getBankForDeposit() {
        return cache.values().stream().filter(bankInfo -> bankInfo.getBankName().compareTo(BankName.OLINDA)==0).findFirst().orElse(null);
    }
}
