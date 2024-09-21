package com.mgbell.post.model.entity;

import lombok.Getter;

@Getter
public enum Cost {
    Type1(8000),
    Type2(12000),
    Type3(16000);

    private final int cost;

    Cost(int cost) {
        this.cost = cost;
    }
}
