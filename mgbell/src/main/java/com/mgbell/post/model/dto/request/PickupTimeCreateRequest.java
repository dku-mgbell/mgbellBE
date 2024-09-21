package com.mgbell.post.model.dto.request;

import com.mgbell.post.model.entity.Week;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PickupTimeCreateRequest {
    @NotNull(message = "week error")
    private Week weekOfWeek;
    @NotNull(message = "startAt error")
    private LocalDateTime startAt;
    @NotNull(message = "endAt error")
    private LocalDateTime endAt;
}
