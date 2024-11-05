package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class IncorrectPassword extends CustomException {
    public IncorrectPassword() {
        super(HttpStatus.BAD_REQUEST, "INCORRECT_PASSWORD");
    }
}
