package com.mgbell.global.error;

import com.mgbell.global.error.model.CustomException;
import com.mgbell.global.error.model.response.ErrorResponse;
import com.mgbell.global.error.service.ErrorService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ControllerAdvisor {
    private final MessageSource messageSource;
    private final ErrorService errorService;
    private boolean isEnabledTest;

    @ExceptionHandler
    protected ResponseEntity<ErrorResponse> handleException(CustomException e, Locale locale) {
        ErrorResponse response = new ErrorResponse(messageSource, locale, e);

        return filter(e, ResponseEntity.status(e.getStatus()).body(response));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodException(MethodArgumentNotValidException e, Locale locale) {
        ErrorResponse response = new ErrorResponse(messageSource, locale, e);
        log.info("method");
        return filter(e, ResponseEntity.status(e.getStatusCode()).body(response));
    }

    private ResponseEntity<ErrorResponse> filter(Throwable t, ResponseEntity<ErrorResponse> entity) {
        ErrorResponse response = entity.getBody();
        if (isEnabledTest && response != null) {
            errorService.logError(response.getTrackingId(), t, response);
        }
        return entity;
    }
}
