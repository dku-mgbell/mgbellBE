package com.mgbell.order.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class PickupTimeOutOfRange extends CustomException {
    public PickupTimeOutOfRange() {
        super(HttpStatus.BAD_REQUEST, "PICKUP_TIME_OUT_OF_RANGE_ERROR");
    }
}
