package com.mgbell.global.s3.exception;

public class OnlyJpegOrPngIsAvailable extends RuntimeException {
    public OnlyJpegOrPngIsAvailable() {
        super("ONLY JPEG OR PNG IS AVAILABLE");
    }
}
