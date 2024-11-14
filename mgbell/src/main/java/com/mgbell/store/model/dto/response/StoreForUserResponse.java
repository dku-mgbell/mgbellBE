package com.mgbell.store.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class StoreForUserResponse {
    private String storeName;
    private String businessRegiNum;
    private int reviewCnt;
    private boolean onSale;
    private String address;
    private String longitude;
    private String latitude;
    private LocalTime startAt;
    private LocalTime endAt;
    private List<String> originalFileDir;
}
