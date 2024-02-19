package com.vulturi.trading.api.util;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProviderClientBuilder;
import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.AWSSecretsManagerAsyncClientBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class AppConfig {

    private final AwsConfig awsConfig;
    @Value("${aws.region}")
    private String region;

    @Bean
    public AWSSecretsManager awsSecretsManager() {
        return AWSSecretsManagerAsyncClientBuilder.standard()
                .withRegion(region)
                .build();
    }


    @Bean
    public AWSCognitoIdentityProvider awsCognitoIdentityProviderClient() {
        return AWSCognitoIdentityProviderClientBuilder.standard()
                .withRegion(awsConfig.getRegion())
                .build();
    }





}