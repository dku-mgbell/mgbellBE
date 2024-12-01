package com.mgbell.user.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class MyPageResponse {
    private String nickName;
    private String name;
    private int orderCount;
    private float carbonReduction;
    private int totalDiscount;
    private List<CurrentOrderResponse> currentOrders;
}
