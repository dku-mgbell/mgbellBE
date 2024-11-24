package com.mgbell.review.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewFilterRequest {
    private Boolean onlyPhotos;
}
