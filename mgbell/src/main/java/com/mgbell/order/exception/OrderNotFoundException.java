package com.mgbell.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("ORDER NOT FOUND");
    }
}
