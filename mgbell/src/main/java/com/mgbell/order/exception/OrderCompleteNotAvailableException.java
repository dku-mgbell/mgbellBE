package com.mgbell.order.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class OrderCompleteNotAvailableException extends CustomException {
    public OrderCompleteNotAvailableException() {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "ORDER_COMPLETE_NOT_AVAILABLE");
    }
}
