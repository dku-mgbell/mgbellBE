package com.mgbell.order.model.entity;

import lombok.Getter;

@Getter
public enum CancleReason {
    NONE("해당 없음"),
    SOLDOUT("품절"),
    ETC("가게 사정");

    private final String reason;

    CancleReason(String reason) {
        this.reason = reason;
    }
}
