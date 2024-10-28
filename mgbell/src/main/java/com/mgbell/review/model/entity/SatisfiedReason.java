package com.mgbell.review.model.entity;

import lombok.Getter;

@Getter
public enum SatisfiedReason {
    KIND_OWNER("친절한 사장님"),
    LOW_PRICE("저렴한 가격"),
    ZERO_WASTE("제로웨이스트 기여"),
    VARIOUS_KINDS("다양한 구성");

    private final String reason;

    SatisfiedReason(String reason) {
        this.reason = reason;
    }
}
