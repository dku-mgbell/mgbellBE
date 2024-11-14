package com.mgbell.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostPreviewForGuestResponse {
    private Long id;
    private String storeName;
    private String bagName;
    private int reviewCnt;
    private boolean onSale;
    private String startAt;
    private String endAt;
    private String address;
    private String longitude;
    private String latitude;
    private int costPrice;
    private int salePrice;
    private int amount;
    private List<String> images;
}
