package com.mgbell.review.model.dto.request;

import com.mgbell.review.model.entity.ReviewScore;
import com.mgbell.review.model.entity.SatisfiedReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewRequest {
    private Long storeId;
    private ReviewScore reviewScore;
    private String content;
    private List<SatisfiedReason> satisfiedReasons = new ArrayList<>();
}
