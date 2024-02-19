package com.vulturi.trading.api.util;

import com.amazonaws.services.secretsmanager.AWSSecretsManager;
import com.amazonaws.services.secretsmanager.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.vulturi.exchanges.connector.model.ExchangeCredentials;
import com.vulturi.trading.api.models.user.Account;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
public class ExchangeCredentialsHandler {
    @Autowired
    private AWSSecretsManager awsSecretsManager;

    @Value("${binance.brokerage-account-encryption-key}")
    private String brokerageAccountEncryptionKey;

    @Value("${kraken.encryption-key}")
    private String krakenAccountEncryptionKey;
    private String BROKERAGE_ACCOUNT = "BROKERAGE_ACCOUNT";

    private String KRAKEN_ACCOUNT = "KRAKEN_ACCOUNT";

    private ObjectMapper objectMapper = new ObjectMapper();

    public ExchangeCredentials getDecryptedCredentialsForAccount(Account account) {
        if (account.getEncryptionKey() == null) {
            return null;
        }
        return decryptCredentials(account.getEncryptionKey(), getEncryptedCredentials(account.getSubAccountId()));
    }

    public ExchangeCredentials getDecryptedCredentialsForBrokerageAccount() {
        return decryptCredentials(brokerageAccountEncryptionKey, getEncryptedCredentials(BROKERAGE_ACCOUNT));
    }

    public ExchangeCredentials getDecryptedCredentialsForKrakenAccount() {
        return decryptCredentials(krakenAccountEncryptionKey, getEncryptedCredentials(KRAKEN_ACCOUNT));
    }


    public void saveCredentials(Account account, ExchangeCredentials exchangeCredentials) {
        innerSaveCredentials(account.getSubAccountId(), account.getEncryptionKey(), exchangeCredentials, true);
    }

    public boolean setBinanceBrokerageCredentials(String encryptionKey, ExchangeCredentials exchangeCredentials) {
        return innerSaveCredentials(BROKERAGE_ACCOUNT, encryptionKey, exchangeCredentials, true);
    }

    public boolean setKrakenCredentials(String encryptionKey, ExchangeCredentials exchangeCredentials) {
        return innerSaveCredentials(KRAKEN_ACCOUNT, encryptionKey, exchangeCredentials, false);
    }


    private boolean innerSaveCredentials(String subAccountId, String encryptionKey, ExchangeCredentials exchangeCredentials, boolean allowUpdate) {
        ExchangeCredentials encryptedCredentials = encryptCredentials(encryptionKey, exchangeCredentials);
        String secretId = getSecretId(subAccountId);
        boolean shouldUpdate = false;
        try {
            DescribeSecretRequest describeSecretRequest = new DescribeSecretRequest();
            describeSecretRequest.setSecretId(secretId);
            awsSecretsManager.describeSecret(describeSecretRequest);
            log.info("Secret already exists");
            shouldUpdate = true;
        } catch (ResourceNotFoundException e) {
            log.info("Secret does not previously exist");
        }
        catch (Exception e) {
            log.error("Error requesting secret", e);
            throw new RuntimeException("Cannot request aws secret", e);
        }

        String serializedExchangeCredentials = null;
        try {
            serializedExchangeCredentials = objectMapper.writeValueAsString(encryptedCredentials);
        }
        catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializing exchange credentials", e);
        }

        if (!shouldUpdate) {
            log.info("Creating secret");
            CreateSecretRequest createSecretRequest = new CreateSecretRequest();
            createSecretRequest.setName(secretId);
            createSecretRequest.setClientRequestToken(UUID.randomUUID().toString());
            createSecretRequest.setSecretString(serializedExchangeCredentials);
            CreateSecretResult createSecretResult = awsSecretsManager.createSecret(createSecretRequest);
            log.info("Secret was created with ARN {}", createSecretResult.getARN());
            return true;
        }
        else if (allowUpdate) {
            log.info("Updating secret");
            UpdateSecretRequest updateSecretRequest = new UpdateSecretRequest();
            updateSecretRequest.setClientRequestToken(UUID.randomUUID().toString());
            updateSecretRequest.setSecretId(secretId);
            updateSecretRequest.setSecretString(serializedExchangeCredentials);
            UpdateSecretResult updateSecretResult = awsSecretsManager.updateSecret(updateSecretRequest);
            log.info("Secret was updated with ARN {}", updateSecretResult.getARN());
            return true;
        }
        return false;
    }

    private ExchangeCredentials encryptCredentials(String encryptionKey, ExchangeCredentials exchangeCredentials) {
        log.info("Encrypting credentials with {}",encryptionKey);
        ExchangeCredentials encryptedExchangeCredentials = new ExchangeCredentials();
        encryptedExchangeCredentials
                .setApiKey(AES.encrypt(exchangeCredentials.getApiKey(), encryptionKey));
        encryptedExchangeCredentials
                .setSecret(AES.encrypt(exchangeCredentials.getSecret(), encryptionKey));
        return encryptedExchangeCredentials;
    }

    private ExchangeCredentials getEncryptedCredentials(String accountId) {
        GetSecretValueRequest getSecretValueRequest = new GetSecretValueRequest();
        getSecretValueRequest.setSecretId(getSecretId(accountId));
        GetSecretValueResult secretValue = awsSecretsManager.getSecretValue(getSecretValueRequest);
        try {
            return objectMapper.readValue(secretValue.getSecretString(), ExchangeCredentials.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot deserialize secret value", e);
        }
    }

    private ExchangeCredentials decryptCredentials(String encryptionKey, ExchangeCredentials exchangeCredentials) {
        ExchangeCredentials decryptedExchangeCredentials = new ExchangeCredentials();
        decryptedExchangeCredentials
                .setApiKey(AES.decrypt(exchangeCredentials.getApiKey(), encryptionKey));
        decryptedExchangeCredentials
                .setSecret(AES.decrypt(exchangeCredentials.getSecret(), encryptionKey));
        return decryptedExchangeCredentials;
    }

    private String getSecretId(String subAccountId) {
        return SpringCtx.getActiveProfile().toLowerCase() + "/exchange-credentials/" + subAccountId;
    }
}
