package com.mgbell.post.model.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PickupTimeUpdateRequest {
    @NotNull(message = "onSale error")
    private boolean onSale;
    @NotNull(message = "startAt error")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime startAt;
    @NotNull(message = "endAt error")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime endAt;
}
