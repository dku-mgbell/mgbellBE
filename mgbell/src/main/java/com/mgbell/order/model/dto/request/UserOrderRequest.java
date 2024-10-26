package com.mgbell.order.model.dto.request;

import com.mgbell.order.model.entity.Payment;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserOrderRequest {
    @NotNull(message = "store id error")
    private Long storeId;
    @NotNull(message = "pick up time error")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime pickupTime;
    private String request;
    private Payment payment = Payment.SPOT;
    @NotNull(message = "amount error")
    private int amount;
}
