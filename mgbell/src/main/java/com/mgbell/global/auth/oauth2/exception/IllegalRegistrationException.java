package com.mgbell.global.auth.oauth2.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class IllegalRegistrationException extends CustomException {
    public IllegalRegistrationException() {
        super(HttpStatus.BAD_REQUEST, "ILLEGAL_REGISTRATION");
    }
}
