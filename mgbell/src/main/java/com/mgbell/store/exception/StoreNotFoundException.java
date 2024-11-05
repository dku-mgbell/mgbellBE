package com.mgbell.store.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class StoreNotFoundException extends CustomException {
    public StoreNotFoundException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "STORE_NOT_FOUND");
    }
}
