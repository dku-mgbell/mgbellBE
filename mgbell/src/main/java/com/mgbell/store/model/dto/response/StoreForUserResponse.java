package com.mgbell.store.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Getter
@Builder
@AllArgsConstructor
public class StoreForUserResponse {
    private String storeName;
    private String businessRegiNum;
    private String address;
    private String longitude;
    private String latitude;
    private LocalTime startAt;
    private LocalTime endAt;
}
