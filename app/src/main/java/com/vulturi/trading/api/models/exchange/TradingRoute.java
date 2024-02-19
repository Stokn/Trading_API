package com.vulturi.trading.api.models.exchange;

import com.google.common.collect.Lists;

import com.vulturi.exchanges.connector.model.OrderSide;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Getter @Setter
public class TradingRoute {

    private List<Pair<String, OrderSide>> pairsToTrade = Lists.newArrayList();

}
