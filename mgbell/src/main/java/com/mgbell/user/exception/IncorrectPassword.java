package com.mgbell.user.exception;

public class IncorrectPassword extends RuntimeException {
    public IncorrectPassword() {
        super("INCORRECT PASSWORD");
    }
}
