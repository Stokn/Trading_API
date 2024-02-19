package com.vulturi.trading.api.util;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "aws", ignoreUnknownFields = false)
public class AwsConfig {


    private String accessKey;


    private String secretKey;


    private String region;


    private final Cognito cognito = new Cognito();


    @Getter
    @Setter
    public static class Cognito {
        private String userPoolId;
        private String appClientId;
        private String appClientSecret;
    }


}