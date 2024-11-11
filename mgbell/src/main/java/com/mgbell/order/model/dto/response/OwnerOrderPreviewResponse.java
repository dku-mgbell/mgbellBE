package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.CancelReason;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.model.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerOrderPreviewResponse {
    private Long orderId;
    private OrderState orderState;
    private CancelReason cancelReason;
    private LocalDateTime orderedTime;
    private String pickupTime;
    private String request;
    private String phoneNumber;
    private int amount;
    private int subTotal;
    private Payment payment;
}
