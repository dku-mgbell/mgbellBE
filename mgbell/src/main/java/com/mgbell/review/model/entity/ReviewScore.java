package com.mgbell.review.model.entity;

import lombok.Getter;

@Getter
public enum ReviewScore {
    NONE("없음"),
    BEST("최고예요"),
    GOOD("좋아요"),
    NOTBAD("적당해요"),
    NOTGOOD("아쉬워요");

    private final String score;

    ReviewScore(String score) {
        this.score = score;
    }
}
