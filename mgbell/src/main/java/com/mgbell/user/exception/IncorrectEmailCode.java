package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class IncorrectEmailCode extends CustomException {
    public IncorrectEmailCode() {
        super(HttpStatus.BAD_REQUEST, "INCORRECT_EMAIL_CODE");
    }
}
