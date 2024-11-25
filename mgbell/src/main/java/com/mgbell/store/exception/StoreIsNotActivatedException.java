package com.mgbell.store.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class StoreIsNotActivatedException extends CustomException {
    public StoreIsNotActivatedException() {
        super(HttpStatus.BAD_REQUEST, "STORE_IS_NOT_ACTIVATED");
    }
}
