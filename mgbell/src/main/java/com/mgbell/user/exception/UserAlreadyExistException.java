package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class UserAlreadyExistException extends CustomException {
    public UserAlreadyExistException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "USER_ALREADY_EXISTS");
    }
}
