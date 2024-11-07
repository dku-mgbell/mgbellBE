package com.mgbell.review.model.dto.response;

import com.mgbell.review.model.entity.ReviewScore;
import com.mgbell.review.model.entity.SatisfiedReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserReviewResponse {
    private Long storeId;
    private Long reviewId;
    private LocalDateTime createdAt;
    private String storeName;
    private ReviewScore reviewScore;
    private String content;
    private String ownerComment;
    private List<SatisfiedReason> satisfiedReasons = new ArrayList<>();
    private List<String> images = new ArrayList<>();
}
