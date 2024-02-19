package com.vulturi.trading.api.services.dca;

import com.google.common.collect.Maps;
import com.vulturi.trading.api.dao.DCADao;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.dca.DCA;
import com.vulturi.trading.api.models.dca.DCAFrequency;
import com.vulturi.trading.api.models.dca.DCAStatus;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.order.TradingOrder;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.exchanges.MultiExchangeTradingService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DCAServiceImpl implements DCAService {

    @Autowired
    private MultiExchangeTradingService multiExchangeTradingService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private DCADao dao;

    private Map<String, List<DCA>> cacheByAccountId = Maps.newConcurrentMap();
    private Map<String, DCA> cache = Maps.newConcurrentMap();

    @PostConstruct
    void init() {
        List<DCA> all = dao.findAll();
        cacheByAccountId = all.stream().collect(Collectors.groupingBy(dca -> dca.getPk().getAccountId()));
        cache = all.stream().collect(Collectors.toMap(DCA::getId,dca->dca));
        log.info("cache refreshed, {} dca(s) saved in cache", cacheByAccountId.size());
    }

    @Override
    public DCA save(DCA dca) {
        DCA savedDCA = dao.save(dca);
        cacheByAccountId.computeIfAbsent(dca.getPk().getAccountId(), s -> new ArrayList<>());
        cacheByAccountId.get(dca.getPk().getAccountId()).add(dca);
        return savedDCA;
    }

    @Override
    public DCA update(Account account, String id, DCAStatus status) throws ApiException {
        List<DCA> dcaList = cacheByAccountId.get(account.getAccountId());
        if(dcaList==null){
            dcaList = dao.findByAccountId(account.getAccountId());
        }
        DCA existingDCA = dcaList.stream().filter(dca -> dca.getId().equals(id)).findFirst().orElse(null);
        if(existingDCA==null){
            throw new ApiException(ApiError.MISSING_VALUE);
        }
        existingDCA.setStatus(status);
        return this.save(existingDCA);
    }

    @Override
    public List<DCA> getActiveDCA(Account account) {
        List<DCA> dcaList = cacheByAccountId.get(account.getAccountId());
        if (dcaList != null) {
            return dcaList.stream().filter(dca -> dca.getStatus().compareTo(DCAStatus.ACTIVE) == 0).toList();
        }
        return List.of();
    }

    @Override
    public List<DCA> getDCA(Account account) {
        List<DCA> dcaList = cacheByAccountId.get(account.getAccountId());
        if (dcaList != null) {
            return dcaList;
        }
        return List.of();
    }


    @Scheduled(cron = "@daily")
    private void dailyBatch() throws ApiException {
        log.info("Executing daily batch...");
        List<DCA> all = getActiveScheduledOrdersByFrequency(DCAFrequency.DAILY);
        convert(all, DCAFrequency.DAILY);

    }


    @Scheduled(cron = "@weekly")
    private void weeklyBatch() throws ApiException {
        log.info("Executing weekly batch...");
        List<DCA> all = getActiveScheduledOrdersByFrequency(DCAFrequency.WEEKLY);
        convert(all, DCAFrequency.WEEKLY);

    }

    @Scheduled(cron = "@monthly")
    private void monthlyBatch() throws ApiException {
        log.info("Executing monthly batch...");
        List<DCA> all = getActiveScheduledOrdersByFrequency(DCAFrequency.DAILY);
        convert(all, DCAFrequency.MONTHLY);

    }


    private List<DCA> getActiveScheduledOrdersByFrequency(DCAFrequency frequency) {
        return dao.findAllByStatusAndFrequency(DCAStatus.ACTIVE, frequency);
    }

    private void convert(List<DCA> dcaList, DCAFrequency frequency) throws ApiException {
        for (DCA dca : dcaList) {
            TradingOrder tradingOrder = toTradingOrder(dca);
            try {
                Account account = accountService.get(dca.getPk().getAccountId());
                multiExchangeTradingService.placeOrder(account,tradingOrder);
                log.info("Executed {} DCA for account: {}", frequency, account.getAccountId());

            } catch (ApiException e) {
                update(accountService.get(dca.getPk().getAccountId()),dca.getId(),DCAStatus.INACTIVE);
                log.error("Cannot execute trade for accountId : {} for {}", dca.getPk().getAccountId(), dca);
            }
        }
    }

    private TradingOrder toTradingOrder(DCA dca){
        TradingOrder tradingOrder = new TradingOrder();
        tradingOrder.setSchedule(true);
        tradingOrder.setFromAsset(dca.getFromCoin());
        tradingOrder.setToAsset(dca.getToCoin());
        tradingOrder.setQuantity(dca.getFromQuantity());
        tradingOrder.setExchange(ExchangePlatform.BINANCE);
        tradingOrder.setPortfolioId(dca.getPk().getPortfolioId());
        tradingOrder.setSchedule(true);
        return tradingOrder;
    }
}
