package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class UserHasNoAuthorityException extends CustomException {
    public UserHasNoAuthorityException() {
        super(HttpStatus.UNAUTHORIZED, "USER_HAS_NO_AUTHORITY");
    }
}
