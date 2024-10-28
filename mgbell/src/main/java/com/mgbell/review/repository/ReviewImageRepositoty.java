package com.mgbell.review.repository;

import com.mgbell.review.model.entity.ReviewImage;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewImageRepositoty extends JpaRepository<ReviewImage, Long> {
}
