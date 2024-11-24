package com.mgbell.favorite.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteResponse {
    private Long id; //판매글 id
    private String storeName;
    private String bagName;
    private boolean onSale;
    private String startAt;
    private String endAt;
    private String address;
    private String longitude;
    private String latitude;
    private int costPrice;
    private int salePrice;
    private int amount;
    private int reviewCnt;
    private String image;
}
