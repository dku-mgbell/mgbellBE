package com.mgbell.post.model.entity;

import lombok.Getter;

@Getter
public enum Week {
    MONDAY(1, "월요일"),
    TUESDAY(2, "화요일"),
    WEDNESDAY(3, "수요일"),
    THURSDAY(4, "목요일"),
    FRIDAY(5, "금요일"),
    SATURDAY(6, "토요일"),
    SUNDAY(7, "일요일");

    private final int order;
    private final String dayOfWeek;

    Week(int order, String dayOfWeek) {
        this.order = order;
        this.dayOfWeek = dayOfWeek;
    }
}
