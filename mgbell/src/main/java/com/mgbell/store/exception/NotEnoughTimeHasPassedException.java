package com.mgbell.store.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class NotEnoughTimeHasPassedException extends CustomException {
    public NotEnoughTimeHasPassedException() {
        super(HttpStatus.BAD_REQUEST, "NOT_ENOUGH_TIME_HAS_PASSED");
    }
}
