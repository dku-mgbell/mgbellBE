package com.mgbell.global.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mgbell.review.model.entity.SatisfiedReason;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class SatisfiedReasonListConverter implements AttributeConverter<List<SatisfiedReason>, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<SatisfiedReason> reviewScores) {
        try {
            return mapper.writeValueAsString(reviewScores);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<SatisfiedReason> convertToEntityAttribute(String data) {
        TypeReference<List<SatisfiedReason>> typeReference = new TypeReference<List<SatisfiedReason>>() {};
        try {
            return mapper.readValue(data, typeReference);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
