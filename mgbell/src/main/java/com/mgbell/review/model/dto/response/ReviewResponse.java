package com.mgbell.review.model.dto.response;

import com.mgbell.review.model.entity.ReviewScore;
import com.mgbell.review.model.entity.SatisfiedReason;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private String userName;
    private LocalDateTime createdAt;
    private ReviewScore reviewScore;
    private String content;
    private List<SatisfiedReason> satisfiedReasons;
    private List<String> images;
    private String ownerComment;
    private LocalDateTime ownerCommentDate;
}
