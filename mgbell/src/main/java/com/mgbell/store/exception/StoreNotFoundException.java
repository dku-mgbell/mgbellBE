package com.mgbell.store.exception;

public class StoreNotFoundException extends RuntimeException {
    public StoreNotFoundException() {
        super("STORE NOT FOUND");
    }
}
