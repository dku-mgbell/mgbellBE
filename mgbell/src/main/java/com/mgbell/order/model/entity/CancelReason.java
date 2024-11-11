package com.mgbell.order.model.entity;

import lombok.Getter;

@Getter
public enum CancelReason {
    NONE("해당 없음"),
    SOLDOUT("품절"),
    ETC("가게 사정");

    private final String reason;

    CancelReason(String reason) {
        this.reason = reason;
    }
}
