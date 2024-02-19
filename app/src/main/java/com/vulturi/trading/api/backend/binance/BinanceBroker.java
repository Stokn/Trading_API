package com.vulturi.trading.api.backend.binance;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;


import com.vulturi.exchanges.connector.model.ExchangeCredentials;
import com.vulturi.exchanges.connector.service.SignatureGenerator;
import com.vulturi.trading.api.exceptions.ApiError;
import com.vulturi.trading.api.exceptions.ApiException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class BinanceBroker {

    private OkHttpClient okHttpClient = new OkHttpClient();
    private String binanceBaseEndpoint = "https://api.binance.com";
    private ObjectMapper objectMapper;

    public BinanceBroker() {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SneakyThrows
    public Collection<BinanceSubAccountCreationResponse> listSubAccounts(ExchangeCredentials decryptedCredentials) {
        String responseAsString =  query(HttpMethod.GET, "/sapi/v1/broker/subAccount", null, decryptedCredentials);
        return Arrays.asList(objectMapper.readValue(responseAsString, BinanceSubAccountCreationResponse[].class));
    }


    public BinanceUniversalTransferResponse transferBetweenSubAccounts(ExchangeCredentials decryptedCredentials, BinanceUniversalTransfer binanceUniversalTransfer) {
        String responseAsString = null;
        try {
            responseAsString = query(HttpMethod.POST, "/sapi/v1/broker/universalTransfer", toMap(binanceUniversalTransfer), decryptedCredentials);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            return objectMapper.readValue(responseAsString, BinanceUniversalTransferResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


    public BinanceUniversalTransferResponse transferFromSubAccountToMaster(ExchangeCredentials decryptedCredentials, BinanceToMasterTransfer binanceToMasterTransfer) {
        String responseAsString = null;
        try {
            responseAsString = query(HttpMethod.POST, "/sapi/v1/broker/universalTransfer", toMap(binanceToMasterTransfer), decryptedCredentials);
        } catch (ApiException e) {

            throw new RuntimeException(e);
        }
        try {
            return objectMapper.readValue(responseAsString, BinanceUniversalTransferResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public BinanceUniversalTransferResponse transferFromMasterToSubAccount(ExchangeCredentials decryptedCredentials, BinanceFromMasterTransfer binanceFromMasterTransfer) {
        String responseAsString = null;
        try {
            responseAsString = query(HttpMethod.POST, "/sapi/v1/broker/universalTransfer", toMap(binanceFromMasterTransfer), decryptedCredentials);
        } catch (ApiException e) {
            throw new RuntimeException(e);
        }
        try {
            return objectMapper.readValue(responseAsString, BinanceUniversalTransferResponse.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public BinanceSubAccountCreationResponse createSubAccountWithTag(ExchangeCredentials decryptedCredentials, String tag) {
        String responseAsString = query(HttpMethod.POST, "/sapi/v1/broker/subAccount", Maps.newHashMap(Map.of("tag", tag)), decryptedCredentials);
        return objectMapper.readValue(responseAsString, BinanceSubAccountCreationResponse.class);
    }

    @SneakyThrows
    public BinanceApiKeyCreationResponse createApiKeyForSubAccount(ExchangeCredentials decryptedCredentials, BinanceApiKeyCreationRequest binanceApiKeyCreationRequest) {
        String responseAsString = query(HttpMethod.POST, "/sapi/v1/broker/subAccountApi", toMap(binanceApiKeyCreationRequest), decryptedCredentials);
        return objectMapper.readValue(responseAsString, BinanceApiKeyCreationResponse.class);
    }

    @SneakyThrows
    public BinanceIpRestrictionResponse updateIpRestriction(ExchangeCredentials decryptedCredentials, BinanceIpRestrictionSwitchRequest whitelistIpRequest) {
        String responseAsString = query(HttpMethod.POST, "/sapi/v2/broker/subAccountApi/ipRestriction", toMap(whitelistIpRequest), decryptedCredentials);
        return objectMapper.readValue(responseAsString, BinanceIpRestrictionResponse.class);
    }

    @SneakyThrows
    public BinanceSpotAssetInfoResponse getSpotAssetInfo(ExchangeCredentials decryptedCredentials, String subAccountId) {
        String responseAsString = query(HttpMethod.GET, "/sapi/v1/broker/subAccount/spotSummary", Maps.newHashMap(Map.of("subAccountId", subAccountId)), decryptedCredentials);
        return objectMapper.readValue(responseAsString, BinanceSpotAssetInfoResponse.class);
    }

    private String getBody(Map<String, Object> params) {
        return params == null ? null : params.entrySet().stream().map((e) -> {
            String var10000 = e.getKey();
            return var10000 + "=" + e.getValue();
        }).collect(Collectors.joining("&"));
    }

    private String query(HttpMethod method, String path, Map<String, Object> params, ExchangeCredentials decryptedCredentials) throws ApiException {
        String responseBodyAsString;
        String url = binanceBaseEndpoint + (path.startsWith("/") ? path : "/" + path);

        params = params == null ? Maps.newHashMap() : params;
        params.put("timestamp", LocalDateTime.now(Clock.systemUTC()).toEpochSecond(ZoneOffset.UTC) * 1000);
        String body = getBody(params);

        String signature = SignatureGenerator.getSignature(body, decryptedCredentials.getSecret());
        body = body + "&signature=" + signature;

        if (method != HttpMethod.POST) {
            url = url + (body == null ? "" : "?" + body);
        }

        Headers requestHeaders = Headers.of(Map.of("X-MBX-APIKEY", decryptedCredentials.getApiKey()));
        RequestBody requestBody = method != HttpMethod.POST ? null : RequestBody.create(body == null ? "" : body, MediaType.parse("application/x-www-form-urlencoded"));
        Request request = (new Request.Builder()).url(url).headers(requestHeaders).method(method.name(), requestBody).build();

        Response response;
        try {
            response = this.okHttpClient.newCall(request).execute();
            responseBodyAsString = response.body() != null ? response.body().string() : null;
        } catch (IOException var13) {
            throw new RuntimeException("Error reaching Binance", var13);
        }

        if (responseBodyAsString != null) {
            if (responseBodyAsString.toLowerCase().contains("Invalid API-key".toLowerCase())) {
                log.info("Invalid api key. Answer is {}", responseBodyAsString);
                throw new ApiException(ApiError.INVALID_CREDENTIALS);
            }
        }

        if (response.code() != 200) {
            log.info("Binance status code is {}", response.code());
            log.error("Error for API Call {}, body {}", url,request.body());
            log.error(responseBodyAsString);
            throw new RuntimeException(responseBodyAsString);
        } else {
            return responseBodyAsString;
        }
    }

    @SneakyThrows
    private Map<String, Object> toMap(Object o) {
        String objectAsString = objectMapper.writeValueAsString(o);
        JsonNode jsonNode = objectMapper.readTree(objectAsString);
        return objectMapper.convertValue(jsonNode, new TypeReference<>() {});
    }

}
