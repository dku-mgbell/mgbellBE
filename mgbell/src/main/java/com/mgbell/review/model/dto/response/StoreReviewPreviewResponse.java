package com.mgbell.review.model.dto.response;

import com.mgbell.review.model.entity.ReviewScore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreReviewPreviewResponse {
    private ReviewScore mostReviewScore;
    private int best;
    private int good;
    private int notGood;
    private int notBad;
    private int reviewCount;
}
