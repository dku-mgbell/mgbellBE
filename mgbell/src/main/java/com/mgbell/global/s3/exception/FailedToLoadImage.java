package com.mgbell.global.s3.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class FailedToLoadImage extends CustomException {
    public FailedToLoadImage() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FAILED_TO_LOAD_IMAGE");
    }
}
