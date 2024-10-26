package com.mgbell.order.model.dto.response;

import com.mgbell.order.model.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OrderRefuseResultResponse {
    private OrderState orderState;
    private String message;
}
