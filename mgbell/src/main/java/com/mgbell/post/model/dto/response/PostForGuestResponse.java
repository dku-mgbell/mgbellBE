package com.mgbell.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostForGuestResponse {
    private Long id;
    private Long storeId;
    private String storeName;
    private String bagName;
    private String description;
    private int reviewCnt;
    private String address;
    private String longitude;
    private String latitude;
    private boolean onSale;
    private int amount;
    private String startAt;
    private String endAt;
    private int costPrice;
    private int salePrice;
    private List<String> images;
}
