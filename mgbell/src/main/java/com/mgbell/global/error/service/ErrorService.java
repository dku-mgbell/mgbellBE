package com.mgbell.global.error.service;

import com.mgbell.global.error.model.CustomError;
import com.mgbell.global.error.model.response.ErrorResponse;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ErrorService {
    private final Map<String, CustomError> errors = new HashMap<>();

    public void logError(String trackingId, Throwable t, ErrorResponse dto) {
        String errorLog = ExceptionUtils.getStackTrace(t);
        errors.put(trackingId, new CustomError(dto, errorLog));
    }

    public String findErrorLog(String trackingId) {
        CustomError log = errors.get(trackingId);
        if (log != null) {
            return log.toString();
        } else {
            return "No error log found for that tracking ID: " + trackingId;
        }
    }
}
