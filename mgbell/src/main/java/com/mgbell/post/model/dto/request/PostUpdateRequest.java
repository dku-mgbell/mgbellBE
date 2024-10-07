package com.mgbell.post.model.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

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
    @NotNull(message = "onSale error")
    private boolean onSale;
    @NotNull(message = "startAt error")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startAt;
    @NotNull(message = "endAt error")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endAt;
}
