package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.dca.DCA;
import com.vulturi.trading.api.models.dca.DCAStatus;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.user.Account;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.web.dto.dca.DCACreateView;
import com.vulturi.trading.api.web.dto.dca.DCAView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DCAFactory {

    @Autowired
    private CoinService coinService;

    public DCA toDCA(Account account, DCACreateView dcaView) throws ApiException {
        Portfolio existingPortfolio = account.getPortfolios().stream().filter(portfolio -> portfolio.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findFirst().orElse(null);
        if(existingPortfolio==null){
            throw new ApiException(ApiError.NO_TRADING_PORTFOLIO_ACTIVATED);
        }
        DCA dca =new DCA();
        dca.getPk().setAccountId(account.getAccountId());
        dca.getPk().setPortfolioId(existingPortfolio.getPortfolioId());
        dca.setFromCoin(dcaView.getFrom());
        dca.setToCoin(dcaView.getTo());
        dca.setFrequency(dcaView.getFrequency());
        dca.setFromQuantity(dcaView.getQuantity());
        dca.setStatus(DCAStatus.ACTIVE);
        return dca;
    }

    public DCAView toView(DCA dca){
        DCAView view = new DCAView();
        view.setAccountId(dca.getPk().getAccountId());
        view.setCreationTs(dca.getPk().getCreationTs());
        view.setStatus(dca.getStatus());
        view.setFrequency(dca.getFrequency());
        view.setQuantity(dca.getFromQuantity());
        view.setFromCoin(coinService.findBySlug(dca.getFromCoin()));
        view.setToCoin(coinService.findBySlug(dca.getToCoin()));
        view.setId(dca.getId());
        return view;
    }




}
