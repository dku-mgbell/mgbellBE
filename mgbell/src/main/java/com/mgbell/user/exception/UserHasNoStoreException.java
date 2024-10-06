package com.mgbell.user.exception;

public class UserHasNoStoreException extends RuntimeException {
    public UserHasNoStoreException() {
        super("HAS NO STORE");
    }
}
