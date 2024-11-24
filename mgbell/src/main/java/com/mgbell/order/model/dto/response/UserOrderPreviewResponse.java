package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderPreviewResponse {
    private Long id; // 판매글 id
    private Long orderId;
    private Long storeId;
    private LocalDateTime orderDateTime;
    private String storeName;
    private String bagName;
    private OrderState orderState;
    private boolean reviewed;
    private int amount;
    private int subTotal;
    private String images;
}
