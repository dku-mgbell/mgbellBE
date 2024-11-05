package com.mgbell.global.error.model;

import com.mgbell.global.error.model.response.ErrorResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class CustomError {
    public final ErrorResponse info;
    public final String errorLog;

    @Override
    public String toString() {
        return String.format("Tracking ID: %s\n" +
                        "Timestamp: %s\n" +
                        "Status: %s\n" +
                        "Code: %s\n" +
                        "Message: %s\n" +
                        "%s",
                info.getTrackingId(), info.getTimestamp(), info.getStatus(),
                info.getCode(), info.getMessage(), errorLog);
    }
}
