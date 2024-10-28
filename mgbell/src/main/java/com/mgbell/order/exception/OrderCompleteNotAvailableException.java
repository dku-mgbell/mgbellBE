package com.mgbell.order.exception;

public class OrderCompleteNotAvailableException extends RuntimeException {
    public OrderCompleteNotAvailableException() {
        super("ORDER COMPLETE NOT AVAILABLE");
    }
}
