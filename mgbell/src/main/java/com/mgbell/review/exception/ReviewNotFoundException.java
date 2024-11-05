package com.mgbell.review.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class ReviewNotFoundException extends CustomException {
    public ReviewNotFoundException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "REVIEW_NOT_FOUND");
    }
}
