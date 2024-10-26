package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderPreviewResponse {
    private Long orderId;
    private Long storeId;
    private LocalDateTime orderDateTime;
    private String storeName;
    private String bagName;
    private OrderState orderState;
    private int amount;
    private int subTotal;
}
