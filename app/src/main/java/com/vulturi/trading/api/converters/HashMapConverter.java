package com.vulturi.trading.api.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
@Slf4j
public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> metaData) {
        String metaDataJson = null;
        try {
            metaDataJson = objectMapper.writeValueAsString(metaData);
        } catch (final JsonProcessingException e) {
            log.error("JSON writing error", e);
        }
        return metaDataJson;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String metaDataJson) {
        if (metaDataJson == null || metaDataJson.equalsIgnoreCase("null")) {
            return null;
        }
        Map<String, Object> metaData = null;
        try {
            metaData = objectMapper.readValue(metaDataJson, Map.class);
        } catch (final IOException e) {
            log.error("JSON reading error", e);
        }

        return metaData;
    }

}