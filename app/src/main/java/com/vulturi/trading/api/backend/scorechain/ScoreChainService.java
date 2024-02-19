package com.vulturi.trading.api.backend.scorechain;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vulturi.trading.api.exceptions.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@Slf4j
public class ScoreChainService implements ScoreChainRequester {
    @Value("${score-chain}")
    private String apiKey;

    protected final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public ScoreChainScoring getScore(GetScoreRequest getScoreRequest) throws JsonProcessingException, ApiException {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.set("X-API-KEY", apiKey);
        HttpEntity request = new HttpEntity(getScoreRequest, headers);
        String scoringAnalysis
                = "https://api.scorechain.com/v1/scoringAnalysis";
        ResponseEntity<String> response = null;
        ScoreChainApiError scoreChainApiError = null;
        try {
            response = restTemplate.postForEntity(scoringAnalysis, request, String.class);
        } catch (HttpClientErrorException e) {
            scoreChainApiError = objectMapper.readValue(e.getResponseBodyAsString(), ScoreChainApiError.class);
            try {
                if (ScoreChainError.valueOf(scoreChainApiError.getError()).compareTo(ScoreChainError.NOT_FOUND_WALLET) == 0) {
                    ScoreChainScoring riskScore = new ScoreChainScoring();
                    riskScore.setScore(null);
                    riskScore.setSeverity(null);
                    return riskScore;
                }
            } catch (Exception exception) {
                throw new ApiException(e.getRawStatusCode(), e.getResponseBodyAsString());
            }
        }
        if (response.getStatusCode() == HttpStatus.OK) {
            ScoreChainResponse scoreChainResponse = objectMapper.readValue(response.getBody(), ScoreChainResponse.class);
            AssignedScore assignedScore = scoreChainResponse.getAnalysis().getAssigned();
            DynamicScore incomingScore = scoreChainResponse.getAnalysis().getIncoming();
            if (assignedScore.getHasResult()) {
                ScoreChainScoring riskScore = new ScoreChainScoring();
                riskScore.setScore(assignedScore.getResult().getScore());
                riskScore.setSeverity(assignedScore.getResult().getSeverity());
                return riskScore;
            } else if (incomingScore.getHasResult()) {
                ScoreChainScoring riskScore = new ScoreChainScoring();
                riskScore.setScore(incomingScore.getResult().getScore());
                riskScore.setSeverity(incomingScore.getResult().getSeverity());
                return riskScore;
            } else {
                throw new RuntimeException("There is no ScoreChain analysis for the request " + getScoreRequest);
            }
        }
        throw new RuntimeException(response.toString());
    }


}
