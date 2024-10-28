package com.mgbell.review.exception;

public class ReviewNotAvailableException extends RuntimeException {
    public ReviewNotAvailableException() {
        super("REVIEW NOT AVAILABLE");
    }
}
