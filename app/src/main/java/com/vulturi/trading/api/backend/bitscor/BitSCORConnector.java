package com.vulturi.trading.api.backend.bitscor;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

@Slf4j@Service
public class BitSCORConnector {

    private OkHttpClient okHttpClient;
    private ObjectMapper objectMapper = new ObjectMapper();


    @Value("${bitscor.client-id}")
    private String clientId;
    @Value("${bitscor.client-secret}")
    private String clientSecret;



    public BitSCORConnector() {
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public Collection<ESGScore> getLastEsgScores() throws IOException {

        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new BitSCORInterceptor(clientId,clientSecret))
                .build();

        Request esgScoreRequest = new Request.Builder()
                .get()
                .url("https://9wljb5had2.execute-api.eu-west-3.amazonaws.com/data-api/v1/esg-scores/sample/last")
                .build();
        Response esgScoreResponse = okHttpClient.newCall(esgScoreRequest).execute();
        log.info("BitSCOR last esg ratings response status is {}", esgScoreResponse.code());
        if (!esgScoreResponse.isSuccessful()) {
            throw new RuntimeException("Cannot get latest scores from BitSCOR. Response code is " + esgScoreResponse.code());
        }
        return List.of(objectMapper.readValue(esgScoreResponse.body().string(), ESGScore[].class));
    }

    public ESGScore getPortfolioESGRating(GetPortfolioAllocationRating getPortfolioAllocationRating) throws IOException {

        okHttpClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(new BitSCORInterceptor(clientId,clientSecret))
                .build();


        RequestBody requestBody = RequestBody.create(objectMapper.writeValueAsString(getPortfolioAllocationRating), MediaType.parse("application/json"));

        Request portfolioRatingRequest = new Request.Builder()
                .post(requestBody)
                .url("https://9wljb5had2.execute-api.eu-west-3.amazonaws.com/data-api/v1/esg-products")
                .build();
        Response portfolioRatingResponse = okHttpClient.newCall(portfolioRatingRequest).execute();
        log.info("BitSCOR esg rating for allocation status is {}", portfolioRatingResponse.code());
        if (!portfolioRatingResponse.isSuccessful()) {
            throw new RuntimeException("Cannot get rating from BitSCOR. Response code is " + portfolioRatingResponse.code());
        }
        return objectMapper.readValue(portfolioRatingResponse.body().string(), ESGScore.class);
    }
}
