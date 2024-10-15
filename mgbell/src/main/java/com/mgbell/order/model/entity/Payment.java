package com.mgbell.order.model.entity;

import lombok.Getter;

@Getter
public enum Payment {
    SPOT("현장 결제"),
    CARD("카드 결제"),
    TOSS("토스페이 결제"),
    KAKAO("카카오페이 결제");

    private final String name;

    Payment(String name) {
        this.name = name;
    }
}
