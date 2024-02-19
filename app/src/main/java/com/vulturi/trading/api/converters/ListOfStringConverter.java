package com.vulturi.trading.api.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Slf4j
public class ListOfStringConverter implements AttributeConverter<Collection<String>, String> {
    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Collection<String> strings) {
        try {
            if (strings == null) {
                return objectMapper.writeValueAsString(new ArrayList<>());
            }
            return objectMapper.writeValueAsString(strings);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot convert list of string to database column");
        }
    }

    @Override
    public Collection<String> convertToEntityAttribute(String s) {

        try {

            if (s == null) {
                return new ArrayList<>();
            }

            return Arrays.asList(objectMapper.readValue(s, String[].class));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot convert database column to list of string");
        }
    }
}