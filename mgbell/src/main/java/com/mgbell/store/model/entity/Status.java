package com.mgbell.store.model.entity;

public enum Status {
    ACTIVE, // 승인 가게
    INACTIVE; // 미승인 가게

    public boolean isActive() {
        return this == ACTIVE;
    }
}
