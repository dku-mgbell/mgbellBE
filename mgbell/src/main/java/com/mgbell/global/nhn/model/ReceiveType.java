package com.mgbell.global.nhn.model;

import lombok.Getter;

@Getter
public enum ReceiveType {
    MRT0("받는사람"),
    MRT1("참조"),
    MRT2("숨은참조");

    private final String typeName;

    ReceiveType(String typeName) {
        this.typeName = typeName;
    }
}