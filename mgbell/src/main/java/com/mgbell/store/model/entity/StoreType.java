package com.mgbell.store.model.entity;

import lombok.Getter;

@Getter
public enum StoreType {
    FOOD("음식점"),
    BAKERY("베이커리"),
    CAFE("카페"),
    DESSERT("디저트"),
    ETC("기타");

    private final String type;

    StoreType(String type) {
        this.type = type;
    }
}
