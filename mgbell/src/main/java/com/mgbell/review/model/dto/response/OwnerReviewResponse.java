package com.mgbell.review.model.dto.response;

import com.mgbell.review.model.entity.SatisfiedReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OwnerReviewResponse {
    private Long reviewId;
    private String userName;
    private String content;
    private String ownerComment;
    private List<SatisfiedReason> satisfiedReasons;
    private List<String> images;
}
