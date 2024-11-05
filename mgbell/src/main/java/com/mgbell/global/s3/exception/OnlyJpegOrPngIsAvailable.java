package com.mgbell.global.s3.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class OnlyJpegOrPngIsAvailable extends CustomException {
    public OnlyJpegOrPngIsAvailable() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "ONLY_JPEG_OR_PNG_IS_AVAILABLE");
    }
}
