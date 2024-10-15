package com.mgbell.order.exception;

public class AmountIsTooBigException extends RuntimeException {
    public AmountIsTooBigException() {
        super("AMOUNT IS TOO BIG");
    }
}
