package com.mgbell.review.exception;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException() {
        super("REVIEW NOT FOUND");
    }
}
