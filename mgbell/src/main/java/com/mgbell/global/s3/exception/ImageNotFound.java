package com.mgbell.global.s3.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class ImageNotFound extends CustomException {
    public ImageNotFound() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "IMAGE_NOT_FOUND");
    }
}
