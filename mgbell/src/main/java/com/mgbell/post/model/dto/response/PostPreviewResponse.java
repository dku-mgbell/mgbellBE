package com.mgbell.post.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class PostPreviewResponse {
    private Long id;
    private String storeName;
    private String bagName;
    private boolean favorite;
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
