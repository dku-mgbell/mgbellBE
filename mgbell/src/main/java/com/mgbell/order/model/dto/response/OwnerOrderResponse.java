package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.CancleReason;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.model.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerOrderResponse {
    private Long orderId;
    private String name;
    private OrderState orderState;
    private String orderedTime;
    private String pickupTime;
    private String request;
    private int amount;
    private int subTotal;
    private Payment payment;
    private CancleReason cancelReason;
}
