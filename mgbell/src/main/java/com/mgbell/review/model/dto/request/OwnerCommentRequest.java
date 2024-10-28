package com.mgbell.review.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerCommentRequest {
    private Long reviewId;
    private String comment;
}
