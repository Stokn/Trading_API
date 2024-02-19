package com.vulturi.trading.api.web.factory;

import com.vulturi.trading.api.models.balance.AssetBalance;
import com.vulturi.trading.api.models.balance.GlobalBalance;
import com.vulturi.trading.api.models.balance.PortfolioBalance;
import com.vulturi.trading.api.services.coin.CoinService;
import com.vulturi.trading.api.services.ohlc.HourlyOHLCService;
import com.vulturi.trading.api.web.dto.balance.AssetBalanceView;
import com.vulturi.trading.api.web.dto.balance.GlobalBalanceView;
import com.vulturi.trading.api.web.dto.balance.PortfolioBalanceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GlobalBalanceFactory {

    @Autowired
    private CoinService coinService;

    @Autowired
    private HourlyOHLCService hourlyOHLCService;

    public  GlobalBalanceView toGlobalBalanceView(GlobalBalance globalBalance) {
        if (globalBalance == null) {
            return null;
        }
        GlobalBalanceView globalBalanceView = new GlobalBalanceView();
        globalBalanceView.setTs(globalBalance.getPk().getTs());
        globalBalanceView.setAccountId(globalBalance.getPk().getAccountId());
        globalBalanceView.setPortfolioBalances(globalBalance.getPortfolioBalances().stream().map(this::toPortfolioBalanceView).toList());
        return globalBalanceView;
    }

    public PortfolioBalanceView toPortfolioBalanceView(PortfolioBalance portfolioBalance) {
        if (portfolioBalance == null) {
            return null;
        }
        PortfolioBalanceView portfolioBalanceView = new PortfolioBalanceView();
        portfolioBalanceView.setPortfolioId(portfolioBalance.getPk().getPortfolioId());
        portfolioBalanceView.setAccountId(portfolioBalance.getPk().getAccountId());
        portfolioBalanceView.setTs(portfolioBalance.getPk().getTs());
        portfolioBalanceView.setPortfolioAccountType(portfolioBalance.getPortfolioAccountType());
        portfolioBalanceView.setAssetBalances(portfolioBalance.getAssetBalances().stream().map(this::toAssetBalanceView).toList());
        for (AssetBalanceView assetBalance : portfolioBalanceView.getAssetBalances()) {
            
        }


        return portfolioBalanceView;
    }

    public  AssetBalanceView toAssetBalanceView(AssetBalance assetBalance){
        if(assetBalance==null){
            return null;
        }
        AssetBalanceView assetBalanceView = new AssetBalanceView();
        assetBalanceView.setAsset(coinService.findBySlug(assetBalance.getPk().getAsset()));
        assetBalanceView.setQuantity(assetBalance.getQuantity());
        assetBalanceView.setTs(assetBalance.getPk().getTs());
        assetBalanceView.setPortfolioId(assetBalance.getPk().getPortfolioId());
        assetBalanceView.setUsdValue(assetBalance.getUsdValue());
        assetBalanceView.setEurValue(assetBalance.getEurValue());
        return assetBalanceView;
    }
}
