package com.mgbell.user.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class UserHasNoStoreException extends CustomException {
    public UserHasNoStoreException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "HAS_NO_STORE");
    }
}
