package com.mgbell.user.model.dto.response;

import com.mgbell.order.model.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CurrentOrderResponse {
    private Long id;
    private String storeName;
    private String pickupTime;
    private OrderState orderState;
}
