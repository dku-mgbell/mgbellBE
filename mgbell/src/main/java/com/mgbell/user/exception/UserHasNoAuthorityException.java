package com.mgbell.user.exception;

public class UserHasNoAuthorityException extends RuntimeException {
    public UserHasNoAuthorityException() {
        super("USER HAS NO AUTHORITY");
    }
}
