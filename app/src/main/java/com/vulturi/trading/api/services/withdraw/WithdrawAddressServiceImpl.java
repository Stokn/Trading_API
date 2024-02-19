package com.vulturi.trading.api.services.withdraw;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.WithdrawAddressDao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.withdraw.WithdrawAddress;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WithdrawAddressServiceImpl implements WithdrawAddressService {
    @Autowired
    private WithdrawAddressDao dao;
    private Map<String, WithdrawAddress> cache = Maps.newConcurrentMap();
    @PostConstruct
    void init() {
        cache = dao.findAll().stream().collect(Collectors.toMap(WithdrawAddress::getAddress, wA -> wA));
        log.info("{} known addresses have been stored in cache", cache.size());
    }


    @Override
    public void saveOrUpdate(WithdrawAddress withdrawAddress) throws ApiException {
        WithdrawAddress existingWithdrawAddress = cache.get(withdrawAddress.getAddress());
        if(existingWithdrawAddress==null){
            dao.save(withdrawAddress);
            cache.put(withdrawAddress.getAddress(),withdrawAddress);
        }
        throw new ApiException(ApiError.WITHDRAW_ADDRESS_ALREADY_EXIST);
    }

    @Override
    public Collection<WithdrawAddress> getByAccountId(String accountId) {
        return cache.values().stream().filter(withdrawAddress -> withdrawAddress.getAccountId().equals(accountId)).toList();
    }

    @Override
    public WithdrawAddress get(String address) {
        return cache.get(address) ;
    }
}
