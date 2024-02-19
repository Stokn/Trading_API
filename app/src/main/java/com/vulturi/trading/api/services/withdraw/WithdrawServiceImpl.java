package com.vulturi.trading.api.services.withdraw;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vulturi.exchanges.connector.model.ApiResult;
import com.vulturi.exchanges.connector.model.Exchange;
import com.vulturi.exchanges.connector.model.MakeWithdrawRequest;
import com.vulturi.exchanges.connector.model.MakeWithdrawResponse;
import com.vulturi.exchanges.connector.service.ExchangeConnector;
import com.vulturi.trading.api.backend.scorechain.*;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.enums.PortfolioAccountType;
import com.vulturi.trading.api.models.portfolio.Portfolio;
import com.vulturi.trading.api.models.withdraw.WithdrawRequest;
import com.vulturi.trading.api.models.withdraw.WithdrawAddress;
import com.vulturi.trading.api.models.withdraw.WithdrawAddressCreationRequest;
import com.vulturi.trading.api.models.withdraw.WithdrawResponse;
import com.vulturi.trading.api.services.account.AccountService;
import com.vulturi.trading.api.services.balance.PortfolioManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WithdrawServiceImpl extends AbstractCryptoTransactionService implements WithdrawService {

    @Autowired
    private ScoreChainService scoreChainService;

    @Autowired
    private WithdrawAddressService withdrawAddressService;

    @Autowired
    private PortfolioManagerService portfolioManagerService;
    @Autowired
    private AccountService accountService;


    @Override
    public void addAddress(WithdrawAddressCreationRequest withdrawAddressCreationRequest) throws JsonProcessingException, ApiException {
        ScoreChainScoring scoreChainScoring = scoreChainService.getScore(toGetScoreRequest(withdrawAddressCreationRequest));
        WithdrawAddress withdrawAddress = toWithdrawAddress(scoreChainScoring, withdrawAddressCreationRequest);
        withdrawAddressService.saveOrUpdate(withdrawAddress);
    }

    private WithdrawAddress toWithdrawAddress(ScoreChainScoring riskScore, WithdrawAddressCreationRequest withdrawAddressCreationRequest) {
        WithdrawAddress withdrawAddress = new WithdrawAddress();
        withdrawAddress.setAccountId(withdrawAddressCreationRequest.getAccountId());
        withdrawAddress.setAddress(withdrawAddressCreationRequest.getAddress());
        withdrawAddress.setNetwork(withdrawAddressCreationRequest.getBlockchain().toString());
        withdrawAddress.setRiskScore(riskScore.getScore());
        withdrawAddress.setRiskSeverity(riskScore.getSeverity());
        return withdrawAddress;
    }

    @Override
    public WithdrawResponse withdraw(WithdrawRequest withdrawRequest) throws ApiException, JsonProcessingException {
        if (withdrawAddressService.get(withdrawRequest.getAddress()) == null) {
            addAddress(toWithdrawAddressCreationRequest(withdrawRequest));
        }
        WithdrawAddress existingWithdrawAddress = withdrawAddressService.get(withdrawRequest.getAddress());
        if (RiskManager.canWithdraw(existingWithdrawAddress.getRiskScore())) {
            MakeWithdrawRequest makeWithdrawRequest = toMakeWithdrawRequest(withdrawRequest);
            WithdrawResponse withdrawResponse = makeWithdraw(withdrawRequest.getAccountId(), makeWithdrawRequest);
            portfolioManagerService.register(withdrawResponse);
            return withdrawResponse;
        } else {
            throw new ApiException(ApiError.SUSPICIOUS_ADDRESS, "You cannot withdraw, suspicious address please contact support at support@stokn.io");
        }
    }

    private WithdrawAddressCreationRequest toWithdrawAddressCreationRequest(WithdrawRequest withdrawRequest) {
        WithdrawAddressCreationRequest withdrawAddressCreationRequest = new WithdrawAddressCreationRequest();
        withdrawAddressCreationRequest.setAccountId(withdrawAddressCreationRequest.getAccountId());
        withdrawAddressCreationRequest.setAddress(withdrawRequest.getAddress());
        withdrawAddressCreationRequest.setCoin(withdrawAddressCreationRequest.getCoin());
        withdrawAddressCreationRequest.setBlockchain(withdrawRequest.getNetwork().getBlockchain());
        return withdrawAddressCreationRequest;
    }

    private MakeWithdrawRequest toMakeWithdrawRequest(WithdrawRequest withdrawRequest) {
        MakeWithdrawRequest makeWithdrawRequest = new MakeWithdrawRequest();
        makeWithdrawRequest.setAddress(withdrawRequest.getAddress());
        makeWithdrawRequest.setNetwork(withdrawRequest.getNetwork().toString());
        makeWithdrawRequest.setAsset(withdrawRequest.getAsset());
        makeWithdrawRequest.setAmount(withdrawRequest.getAmount());
        return makeWithdrawRequest;
    }

    private GetScoreRequest toGetScoreRequest(WithdrawAddressCreationRequest withdrawAddressCreationRequest) {
        GetScoreRequest getScoreRequest = new GetScoreRequest();
        getScoreRequest.setCoin(ScoreChainCoin.ALL);
        getScoreRequest.setBlockchain(withdrawAddressCreationRequest.getBlockchain());
        getScoreRequest.setObjectType(ScoreChainObjType.ADDRESS);
        getScoreRequest.setAnalysisType(ScoreChainAnalysisType.INCOMING);
        return getScoreRequest;
    }


    public WithdrawResponse makeWithdraw(String accountId, MakeWithdrawRequest makeWithdrawRequest) throws ApiException {
        ExchangeConnector exchangeConnector = getExchangeConnector(Exchange.BINANCE_SPOT);
        Portfolio impactedPortfolio = accountService.get(accountId).getPortfolios().stream().filter(portfolio -> portfolio.getPortfolioAccountType().compareTo(PortfolioAccountType.TRADING) == 0).findAny().orElseThrow(() -> new ApiException(ApiError.NO_TRADING_PORTFOLIO_ACTIVATED));
        ApiResult<MakeWithdrawResponse> makeWithdrawResponseApiResult = exchangeConnector.makeWithdraw(makeWithdrawRequest);
        if (makeWithdrawResponseApiResult.getSuccess()) {
            return toWithdrawResponse(impactedPortfolio, makeWithdrawRequest);
        } else {
            throw new ApiException(409, makeWithdrawResponseApiResult.getErrorCode(), makeWithdrawResponseApiResult.getErrorMessage());
        }
    }

    private WithdrawResponse toWithdrawResponse(Portfolio impactedPortfolio, MakeWithdrawRequest makeWithdrawRequest) {
        WithdrawResponse withdrawResponse = new WithdrawResponse();
        withdrawResponse.setAccountId(impactedPortfolio.getAccountId());
        withdrawResponse.setPortfolioId(impactedPortfolio.getPortfolioId());
        withdrawResponse.setAmount(makeWithdrawRequest.getAmount());
        withdrawResponse.setNetwork(makeWithdrawRequest.getNetwork());
        withdrawResponse.setAsset(makeWithdrawRequest.getAsset());
        withdrawResponse.setAddress(makeWithdrawRequest.getAddress());
        return withdrawResponse;
    }
}
