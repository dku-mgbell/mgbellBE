package com.mgbell.user.exception;

public class UserAlreadyExistException extends RuntimeException {
    public UserAlreadyExistException() {
        super("USER ALREADY EXISTS");
    }
}
