package com.vulturi.trading.api.services.exchanges;


import com.google.common.collect.Lists;
import com.vulturi.exchanges.connector.model.*;
import com.vulturi.trading.api.backend.binance.BinanceSymbol;
import com.vulturi.trading.api.backend.binance.BinanceTicker;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import com.vulturi.trading.api.models.enums.ExchangePlatform;
import com.vulturi.trading.api.models.exchange.TradingRoute;
import com.vulturi.trading.api.models.exchange.TradingTransaction;
import com.vulturi.trading.api.models.order.TradingOrder;
import com.vulturi.trading.api.models.transaction.Transaction;
import com.vulturi.trading.api.models.transaction.TransactionSide;
import com.vulturi.trading.api.models.transaction.TransactionType;
import com.vulturi.trading.api.services.marketplace.BinanceService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
@Slf4j
public class BinanceExchangeService extends AbstractExchangeService implements TradingService {


    @Autowired
    private BinanceService binanceService;

    private boolean canTrade(String symbol) {
        return binanceService.canTrade(symbol);
    }

    private Pair<BigDecimal, Boolean> getNewQtyAndIsBaseQty(SendOrderResponse sendOrderResponse, OrderSide side, BinanceSymbol binanceSymbol, String nextSymbol) {
        String obtainedCoin = side == OrderSide.BUY ? binanceSymbol.getBaseAsset() : binanceSymbol.getQuoteAsset();
        BigDecimal obtainedQty = side == OrderSide.BUY ? sendOrderResponse.getExecutedQuantity() : sendOrderResponse.getCumulativeQuoteQty();
        if (nextSymbol.startsWith(obtainedCoin)) {
            return Pair.of(obtainedQty.setScale(8, RoundingMode.HALF_DOWN), true);
        } else {
            return Pair.of(obtainedQty.setScale(8, RoundingMode.HALF_DOWN), false);
        }
    }


    private TradingRoute getBestRoute(String fromCoin, String toCoin) throws ApiException {
        TradingRoute tradingRoute = new TradingRoute();

        //we first try to see if a single order could solve it
        boolean isSimpleRoute = true;
        OrderSide orderSide = OrderSide.SELL;
        String symbolToTrade = fromCoin + toCoin;
        log.info("Initially trying to sell {}", symbolToTrade);

        //this is in the case the SELL order is not possible, we try to buy the opposite symbol
        if (!canTrade(symbolToTrade)) {
            log.warn("Symbol {} cannot be traded", symbolToTrade);
            symbolToTrade = toCoin + fromCoin;
            orderSide = OrderSide.BUY;
            log.info("Will instead try to buy {}", symbolToTrade);
            //if the 'buy order' symbol does not exist either, then we need a complex route (multi orders)
            if (!canTrade(symbolToTrade)) {
                log.info("Cannot either trade buy side pair {} - Trying to find a more complex route", symbolToTrade);
                isSimpleRoute = false;
            }
        }

        //if simple route, we return it
        if (isSimpleRoute) {
            tradingRoute.getPairsToTrade().add(Pair.of(symbolToTrade, orderSide));
            return tradingRoute;
        }

        throw new ApiException(ApiError.NO_TRADING_PORTFOLIO_ACTIVATED);
        /*
         //We enter the complex mode
        Collection<TradingRoute> complexRoutes = getComplexRoutes(fromCoin, toCoin);
        //we find one route among the potential ones
        return getBestRouteAmongComplexRoutes(complexRoutes);
         */
    }

    private TradingRoute getBestRouteAmongComplexRoutes(Collection<TradingRoute> complexTradingRoutes) {
        if (complexTradingRoutes == null) {
            return null;
        }
        return complexTradingRoutes.stream().sorted((r1, r2) -> getLiquidityForRoute(r2).compareTo(getLiquidityForRoute(r1))).limit(1).findFirst().orElse(null);
    }

    private BigDecimal getLiquidityForRoute(TradingRoute route) {
        String toCoin = binanceService.getSymbol(route.getPairsToTrade().get(route.getPairsToTrade().size() - 1).getKey()).getQuoteAsset();
        BigDecimal liquidity = BigDecimal.ZERO;
        List<BigDecimal> converters = Lists.newArrayList();
        //we iterate reverse so that we store tickers to convert the volumes in final coins
        for (int pairIndex = route.getPairsToTrade().size() - 1; pairIndex >= 0; pairIndex--) {
            Pair<String, OrderSide> pairToTrade = route.getPairsToTrade().get(pairIndex);
            String symbol = pairToTrade.getKey();
            BinanceTicker tickerForSymbol = binanceService.getBinanceTicker(symbol);
            if (symbol.endsWith(toCoin)) {
                liquidity = liquidity.add(tickerForSymbol.getQuoteVolume());
            } else {
                liquidity = liquidity.add(tickerForSymbol.getQuoteVolume().multiply(converters.stream().reduce(BigDecimal::multiply).orElseThrow(() -> new RuntimeException("Cannot compute liquidity for " + symbol))));
            }
            converters.add(tickerForSymbol.getLastPrice());
        }
        return liquidity;
    }

    private Collection<TradingRoute> getComplexRoutes(String fromCoin, String toCoin) {
        log.info("Entering the complex route mode");
        Collection<BinanceSymbol> allSymbols = binanceService.getAllSymbols();
        List<BinanceSymbol> startSymbols = allSymbols.stream().filter(s -> s.getBaseAsset().equals(fromCoin) || s.getQuoteAsset().equals(fromCoin)).toList();
        List<BinanceSymbol> endSymbols = allSymbols.stream().filter(s -> s.getBaseAsset().equals(toCoin) || s.getQuoteAsset().equals(toCoin)).toList();

        Collection<TradingRoute> potentialRoutes = Lists.newArrayList();
        for (BinanceSymbol startSymbol : startSymbols) {
            List<String> neededEndSymbols = Lists.newArrayList(
                    (startSymbol.getBaseAsset().equals(fromCoin) ? startSymbol.getQuoteAsset() : startSymbol.getBaseAsset()) + toCoin,
                    toCoin + (startSymbol.getBaseAsset().equals(fromCoin) ? startSymbol.getQuoteAsset() : startSymbol.getBaseAsset())
            );

            for (String neededEndSymbol : neededEndSymbols) {
                if (endSymbols.stream().anyMatch(s -> s.getSymbol().equals(neededEndSymbol))) {
                    TradingRoute route = new TradingRoute();
                    route.getPairsToTrade().add(Pair.of(startSymbol.getSymbol(), startSymbol.getBaseAsset().equals(fromCoin) ? OrderSide.SELL : OrderSide.BUY));
                    route.getPairsToTrade().add(Pair.of(neededEndSymbol, neededEndSymbol.startsWith(toCoin) ? OrderSide.BUY : OrderSide.SELL));
                    potentialRoutes.add(route);
                }
            }
        }
        return potentialRoutes;
    }


    @Override
    public TradingTransaction trade(TradingOrder tradingOrder) throws ApiException {
        log.info("Handling trading order {}", tradingOrder);
        TradingRoute bestTradingRoute = getBestRoute(tradingOrder.getFromAsset(), tradingOrder.getToAsset());
        Pair<String, OrderSide> firstPairToTrade = bestTradingRoute.getPairsToTrade().iterator().next();
        String firstSymbolToTrade = firstPairToTrade.getKey();
        OrderSide firstOrderSide = firstPairToTrade.getValue();
        BigDecimal fromQty = tradingOrder.getQuantity();
        // todo: adjust fromQuantity to take fees on trade.
        BigDecimal orderQuantity = fromQty;

        Pair<BigDecimal, Boolean> qtyAndIsBaseQty = Pair.of(orderQuantity, true);

        if (firstOrderSide == OrderSide.BUY) {
            qtyAndIsBaseQty = Pair.of(orderQuantity, false);
        }

        TradingTransaction tradingTransaction = new TradingTransaction();
        tradingTransaction.getPk().setAccountId(tradingOrder.getAccountId());
        tradingTransaction.getPk().setPortfolioId(tradingOrder.getPortfolioId());
        tradingTransaction.setFromCoin(tradingOrder.getFromAsset());
        tradingTransaction.setToCoin(tradingOrder.getToAsset());
        tradingTransaction.setToCoin(tradingTransaction.getToCoin());
        tradingTransaction.setOrderId(tradingOrder.getOrderId());

        // Execute Trade
        boolean isFirstOrder = true;
        for (int pairToTradeIndex = 0; pairToTradeIndex < bestTradingRoute.getPairsToTrade().size(); pairToTradeIndex++) {

            Pair<String, OrderSide> pairToTrade = bestTradingRoute.getPairsToTrade().get(pairToTradeIndex);
            String symbol = pairToTrade.getKey();
            BinanceSymbol binanceSymbol = binanceService.getSymbol(symbol);
            OrderSide orderSide = pairToTrade.getValue();
            ApiResult<SendOrderResponse> sendOrderResponseApiResult = sendOrder(false, orderSide, qtyAndIsBaseQty.getKey(), symbol, qtyAndIsBaseQty.getValue());
            log.info("Send order response from {} is {}", getExchangePlatform(), sendOrderResponseApiResult);
            if (sendOrderResponseApiResult.getSuccess() != Boolean.TRUE) {
                tradingTransaction.setSuccess(false);
                // todo save trading transaction in trading transactionService;
                // todo in case where trade is not completed
            }

            LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

            Transaction creditTradeTransaction = orderSide == OrderSide.SELL ?
                    toTransaction(tradingOrder.getAccountId(),tradingOrder.getPortfolioId(), tradingTransaction.getId(), TransactionSide.CREDIT,TransactionType.TRADE,tradingOrder.getToAsset(),sendOrderResponseApiResult.getData().getCumulativeQuoteQty()):
                    toTransaction(tradingOrder.getAccountId(),tradingOrder.getPortfolioId(), tradingTransaction.getId(), TransactionSide.CREDIT,TransactionType.TRADE,tradingOrder.getToAsset(),sendOrderResponseApiResult.getData().getExecutedQuantity());


            Transaction debitTradeTransaction = orderSide == OrderSide.SELL ?
                    toTransaction(tradingOrder.getAccountId(),tradingOrder.getPortfolioId(), tradingTransaction.getId(), TransactionSide.DEBIT,TransactionType.TRADE,tradingOrder.getFromAsset(),sendOrderResponseApiResult.getData().getExecutedQuantity()):
                    toTransaction(tradingOrder.getAccountId(),tradingOrder.getPortfolioId(), tradingTransaction.getId(), TransactionSide.DEBIT,TransactionType.TRADE,tradingOrder.getFromAsset(),sendOrderResponseApiResult.getData().getCumulativeQuoteQty());

            tradingTransaction.getTransactionIds().addAll(Arrays.asList(creditTradeTransaction.getId(),debitTradeTransaction.getId()));
            portfolioManagerService.saveAll(Arrays.asList(debitTradeTransaction, creditTradeTransaction));

            if (isFirstOrder) {

                Transaction debitFeesTransaction = toTransaction(tradingOrder.getAccountId(),tradingOrder.getPortfolioId(), tradingTransaction.getId(), TransactionSide.DEBIT,TransactionType.FEES,tradingOrder.getFromAsset(),BigDecimal.ZERO);
                Transaction creditFeesTransaction = toTransaction(this.stoknAccountId,this.portfolioIdFees, tradingTransaction.getId(), TransactionSide.CREDIT,TransactionType.FEES,tradingOrder.getFromAsset(),BigDecimal.ZERO);
                tradingTransaction.getTransactionIds().addAll(Arrays.asList(debitFeesTransaction.getId(),creditFeesTransaction.getId()));
                portfolioManagerService.saveAll(Arrays.asList(debitFeesTransaction, creditFeesTransaction));
                // todo take fees only on first trade
            }

            tradingTransaction.setSuccess(true);


            isFirstOrder = false;
            if (bestTradingRoute.getPairsToTrade().size() > pairToTradeIndex + 1) {
                qtyAndIsBaseQty = getNewQtyAndIsBaseQty(sendOrderResponseApiResult.getData(), orderSide, binanceSymbol, bestTradingRoute.getPairsToTrade().get(pairToTradeIndex + 1).getKey());
            }
        }
        return tradingTransaction;
    }


    public ApiResult<SendOrderResponse> sendOrder(boolean forTest, OrderSide orderSide, BigDecimal quantity, String asset, boolean isBaseQty) {
        BigDecimal adjustedQuantity = isBaseQty ? adjustOrderQuantity(quantity, binanceService.getSymbol(asset)) : quantity.setScale(8, RoundingMode.HALF_DOWN);
        log.info("Sending market order: {} {} -> {} {}", orderSide, quantity, adjustedQuantity, asset);
        SendOrderRequest sendOrderRequest = new SendOrderRequest();
        sendOrderRequest.setType(OrderType.MARKET);
        sendOrderRequest.setSide(orderSide);
        if (isBaseQty) {
            sendOrderRequest.setSize(adjustedQuantity);
        } else {
            sendOrderRequest.setNotional(adjustedQuantity);
        }
        sendOrderRequest.setInstrument(asset);
        log.info("Sending order {}", sendOrderRequest);
        if (!forTest) {
            return getExchangeConnector().sendOrder(sendOrderRequest);
        } else {
            return getExchangeConnector().sendTestOrder(sendOrderRequest);
        }
    }

    private BigDecimal adjustOrderQuantity(BigDecimal originalQty, BinanceSymbol symbolInfo) {
        log.info("Trying to adjust qty for symbol {}", symbolInfo);
        log.info("Original quantity to adjust {}", originalQty);
        if (symbolInfo == null) {
            return originalQty;
        }
        BigDecimal adjQty = originalQty.abs().divide(symbolInfo.getStepSize(), 0, RoundingMode.DOWN).multiply(symbolInfo.getStepSize()).multiply(BigDecimal.valueOf(originalQty.signum()));
        log.info("Adjusted quantity is {} for orginal quantity of {}", originalQty, originalQty);
        return adjQty;
    }


    @Override
    protected ExchangePlatform getExchangePlatform() {
        return ExchangePlatform.BINANCE;
    }
}
