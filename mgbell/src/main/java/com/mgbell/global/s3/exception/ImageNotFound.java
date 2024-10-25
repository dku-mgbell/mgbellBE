package com.mgbell.global.s3.exception;

public class ImageNotFound extends RuntimeException {
    public ImageNotFound() {
        super("IMAGE NOT FOUND");
    }
}
