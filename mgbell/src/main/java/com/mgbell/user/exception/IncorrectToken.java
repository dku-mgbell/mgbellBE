package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class IncorrectToken extends CustomException {
    public IncorrectToken() {
        super(HttpStatus.BAD_REQUEST, "INCORRECT_TOKEN");
    }
}
