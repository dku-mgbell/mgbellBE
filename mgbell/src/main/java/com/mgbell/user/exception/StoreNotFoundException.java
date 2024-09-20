package com.mgbell.user.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException() {
        super("STORE NOT FOUND");
    }
}
