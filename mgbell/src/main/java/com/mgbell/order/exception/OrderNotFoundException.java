package com.mgbell.order.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class OrderNotFoundException extends CustomException {
    public OrderNotFoundException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "ORDER_NOT_FOUND");
    }
}
