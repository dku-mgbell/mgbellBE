package com.mgbell.order.model.dto.request;

import com.mgbell.order.model.entity.CancelReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerOrderCancleRequest {
    private CancelReason cancleReason;
}
