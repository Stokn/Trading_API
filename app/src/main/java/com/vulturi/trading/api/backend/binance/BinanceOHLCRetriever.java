package com.vulturi.trading.api.backend.binance;



import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.Maps;
import com.vulturi.trading.api.models.marketplace.ohlc.Frequency;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLC;
import com.vulturi.trading.api.models.marketplace.ohlc.OHLCSource;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Component@Slf4j
public class BinanceOHLCRetriever  implements OHLCRetriever {

    private OkHttpClient client = new OkHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();


    public OHLCSource getSource() {
        return OHLCSource.BINANCE;
    }

    @Override
    public OHLC retrieveFromBackend(Frequency frequency, BinanceSymbol symbol, LocalDateTime ts) {
        String url = "https://api.binance.com/api/v3/klines";
        long tsToEpochMilliseconds = ts.toEpochSecond(ZoneOffset.UTC) * 1000;
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("symbol", symbol.getSymbol());
        parameters.put("interval", convertFrequencyToInterval(frequency));
        parameters.put("startTime", tsToEpochMilliseconds);

        Request request = new Request.Builder().url(url + "?" + parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue().toString()).collect(Collectors.joining("&"))).build();
        try {
            Response response = client.newCall(request).execute();
            ArrayNode rootNode = (ArrayNode) objectMapper.readTree(response.body().string());
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                ArrayNode ohlcNode = (ArrayNode) elements.next();
                long timestamp = ohlcNode.get(0).longValue();
                if (timestamp == tsToEpochMilliseconds) {
                    LocalDateTime tsFinal = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
                    OHLC ohlc = OHLC.build(frequency, symbol.getSymbol(), tsFinal, getSource(), null, null, null, null, null);
                    ohlc.setOpen(new BigDecimal(ohlcNode.get(1).asText()));
                    ohlc.setHigh(new BigDecimal(ohlcNode.get(2).asText()));
                    ohlc.setLow(new BigDecimal(ohlcNode.get(3).asText()));
                    ohlc.setClose(new BigDecimal(ohlcNode.get(4).asText()));
                    ohlc.setVolume(new BigDecimal(ohlcNode.get(5).asText()));
                    return ohlc;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot get ohlc from binance", e);
        }
        return null;
    }

    @Override
    public Collection<OHLC> retrieveHistoricalFromBackend(Frequency frequency, BinanceSymbol symbol, LocalDateTime ts) throws InterruptedException {
        LocalDateTime tmp = ts;
        LocalDateTime now = adjustNowTs(frequency, LocalDateTime.now(Clock.systemUTC()));
        Collection<OHLC> ohlcCollection = new ArrayList<>();
        while (tmp.compareTo(now) < 0) {
            Collection<OHLC> ohlcs = getOhlcs(frequency, symbol, tmp);
            ohlcCollection.addAll(ohlcs);
            OHLC lastOhlc = ohlcs.stream().max(Comparator.comparing(ohlc -> ohlc.getPk().getTs())).orElse(null);
            Thread.sleep(1000);
            if (lastOhlc!=null){
                tmp = lastOhlc.getPk().getTs();
            }
            else {
                return ohlcCollection;
            }
        }
        return ohlcCollection;

    }

    private LocalDateTime adjustNowTs(Frequency frequency, LocalDateTime now) {
        switch (frequency) {
            case DAY -> now = now.truncatedTo(ChronoUnit.DAYS);
            case HOUR -> now = now.truncatedTo(ChronoUnit.HOURS);
            case MINUTE -> now = now.truncatedTo(ChronoUnit.MINUTES);
            case SECOND -> now = now.truncatedTo(ChronoUnit.SECONDS);
        }
        return now;
    }




    @NotNull
    private Collection<OHLC> getOhlcs(Frequency frequency, BinanceSymbol symbol, LocalDateTime ts) {
        String url = "https://api.binance.com/api/v3/klines";
        log.info("{}",ts);
        long tsToEpochMilliseconds = ts.toEpochSecond(ZoneOffset.UTC) * 1000;
        Map<String, Object> parameters = Maps.newHashMap();
        parameters.put("symbol", symbol.getSymbol());
        parameters.put("interval", convertFrequencyToInterval(frequency));
        parameters.put("startTime", tsToEpochMilliseconds);
        parameters.put("limit", 1000);


        Request request = new Request.Builder().url(url + "?" + parameters.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue().toString()).collect(Collectors.joining("&"))).build();
        try {
            Response response = client.newCall(request).execute();
            ArrayNode rootNode = (ArrayNode) objectMapper.readTree(response.body().string());
            Collection<OHLC> ohlcCollection = new ArrayList<>();
            Iterator<JsonNode> elements = rootNode.elements();
            while (elements.hasNext()) {
                ArrayNode ohlcNode = (ArrayNode) elements.next();
                long timestamp = ohlcNode.get(0).longValue();
                LocalDateTime tsFinal = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
                OHLC ohlc = OHLC.build(frequency, symbol.getSymbol(), tsFinal, getSource(), null, null, null, null, null);
                ohlc.setOpen(new BigDecimal(ohlcNode.get(1).asText()));
                ohlc.setHigh(new BigDecimal(ohlcNode.get(2).asText()));
                ohlc.setLow(new BigDecimal(ohlcNode.get(3).asText()));
                ohlc.setClose(new BigDecimal(ohlcNode.get(4).asText()));
                ohlc.setVolume(new BigDecimal(ohlcNode.get(5).asText()));
                ohlcCollection.add(ohlc);
            }
            return ohlcCollection;
        } catch (IOException e) {
            throw new RuntimeException("Cannot get ohlc from binance", e);
        }
    }

    private String convertFrequencyToInterval(Frequency frequency) {
        switch (frequency) {
            case DAY:
                return "1d";
            case HOUR:
                return "1h";
            case MINUTE:
                return "1m";
            case SECOND:
                return "1s";
            default:
                throw new UnsupportedOperationException("Cannot get binance equivalence for frequency " + frequency);
        }
    }
}
