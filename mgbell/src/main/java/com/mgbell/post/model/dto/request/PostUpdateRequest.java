package com.mgbell.post.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostUpdateRequest {
    @NotBlank(message = "bagName error")
    private String bagName;
    @NotBlank(message = "description error")
    private String description;
    @NotNull(message = "cost price error")
    private int costPrice;
    @NotNull(message = "sale price error")
    private int salePrice;
    @NotNull(message = "amount")
    private int amount;

    @NotNull(message = "pickupTime error")
    @Valid
    private PickupTimeUpdateRequest pickupTime;
}
