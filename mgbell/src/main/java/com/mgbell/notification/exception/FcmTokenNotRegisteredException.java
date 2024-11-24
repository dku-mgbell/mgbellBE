package com.mgbell.notification.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class FcmTokenNotRegisteredException extends CustomException {
    public FcmTokenNotRegisteredException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "FCM_TOKEN_NOT_REGISTERED");
    }
}
