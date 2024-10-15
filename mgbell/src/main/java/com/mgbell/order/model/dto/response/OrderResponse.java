package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class OrderResponse {
    private Long orderId;
    private Long storeId;
    private String storeName;
    private String address;
    private String pickupTime;
    private String request;
    private int amount;
    private Payment payment;
}
