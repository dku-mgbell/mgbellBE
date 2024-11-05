package com.mgbell.order.exception;

import com.mgbell.global.error.model.CustomException;
import org.springframework.http.HttpStatus;

public class AmountIsTooBigException extends CustomException {
    public AmountIsTooBigException() {
        super(HttpStatus.BAD_REQUEST, "AMOUNT_IS_TOO_BIG");
    }
}
