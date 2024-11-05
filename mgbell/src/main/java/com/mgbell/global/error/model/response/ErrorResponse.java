package com.mgbell.global.error.model.response;

import com.mgbell.global.error.model.CustomException;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

@Getter
public class ErrorResponse {
    private final String trackingId;
    private final String timestamp;
    private final HttpStatus status;
    private final String code;
    private final List<Object> message;

    public ErrorResponse(MessageSource messageSource, Locale locale, CustomException e) {
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.status = e.getStatus();
        this.code = e.getCode();
        this.message = e.getMessages(messageSource, locale);
    }

    public ErrorResponse(MessageSource messageSource, Locale locale,
                            MethodArgumentNotValidException e) {
        this.timestamp = LocalDateTime.now().toString();
        this.trackingId = UUID.randomUUID().toString();
        this.status = HttpStatus.resolve(e.getStatusCode().value());
        this.code = e.getClass().getSimpleName();
        this.message = List.of(e.getDetailMessageArguments(messageSource, locale));
    }
}
