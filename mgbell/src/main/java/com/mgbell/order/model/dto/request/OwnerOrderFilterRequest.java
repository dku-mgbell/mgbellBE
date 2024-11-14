package com.mgbell.order.model.dto.request;

import com.mgbell.order.model.entity.OrderState;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OwnerOrderFilterRequest {
    private OrderState state;
}
