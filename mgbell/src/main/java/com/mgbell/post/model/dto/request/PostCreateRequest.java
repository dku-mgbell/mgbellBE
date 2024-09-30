package com.mgbell.post.model.dto.request;

import com.mgbell.post.model.entity.Cost;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;

@Getter
@AllArgsConstructor
public class PostCreateRequest {

    @NotBlank(message = "title error")
    private String title;
    @NotBlank(message = "content error")
    private String content;
    @NotNull(message = "cost error")
    private Cost cost;
    @NotNull(message = "amount")
    private int amount;

    @NotNull(message = "pickupTime error")
    @Valid
    private ArrayList<PickupTimeCreateRequest> pickupTime;

}
