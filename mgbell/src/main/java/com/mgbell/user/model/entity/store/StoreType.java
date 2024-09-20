package com.mgbell.user.model.entity.store;

import lombok.Getter;

@Getter
public enum StoreType {
    FOOD("음식점"),
    BAKERY("베이커리"),
    CAFE("카페"),
    BUFFET("뷔페"),
    ETC("기타");

    private final String type;

    StoreType(String type) {
        this.type = type;
    }
}
