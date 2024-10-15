package com.mgbell.order.model.entity;

import lombok.Getter;

@Getter
public enum OrderState {
    REQUESTED("요청됨"),
    ACCEPTED("수락됨"),
    COMPLETED("완료됨"),
    USER_CANCELED("취소됨"),
    OWNER_REFUSED("거절됨");

    private final String state;

    OrderState(String state) {
        this.state = state;
    }
}
