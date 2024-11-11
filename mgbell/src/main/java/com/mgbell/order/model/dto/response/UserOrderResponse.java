package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.CancelReason;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.model.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class UserOrderResponse {
    private Long orderId;
    private Long storeId;
    private String storeName;
    private String bagName;
    private OrderState orderState;
    private boolean reviewed;
    private LocalDateTime orderDateTime;
    private String address;
    private Payment payment;
    private int amount;
    private String pickupTime;
    private int subTotal;
    private String request;
    private CancelReason cancelReason;
    private String images;
}
