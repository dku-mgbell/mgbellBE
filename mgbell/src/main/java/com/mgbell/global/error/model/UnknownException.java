package com.mgbell.global.error.model;

import org.springframework.http.HttpStatus;

public class UnknownException extends CustomException {
    public UnknownException(Throwable e) {
        super(e, HttpStatus.INTERNAL_SERVER_ERROR, "UNKNOWN_ERROR");
    }
}
