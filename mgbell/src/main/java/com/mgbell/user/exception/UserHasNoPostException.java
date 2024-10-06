package com.mgbell.user.exception;

public class UserHasNoPostException extends RuntimeException {
    public UserHasNoPostException() {
        super("HAS NO POST");
    }
}
