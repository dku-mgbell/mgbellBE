package com.mgbell.store.exception;

public class NotEnoughTimeHasPassedException extends RuntimeException {
    public NotEnoughTimeHasPassedException() {
        super("NOT ENOUGH TIME HAS PASSED");
    }
}
