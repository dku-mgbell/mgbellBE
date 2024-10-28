package com.mgbell.review.repository;

import com.mgbell.review.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByUserId(Pageable pageable, Long userId);
    Page<Review> findByStoreId(Pageable pageable, Long storeId);
    Optional<Review> findByUserIdAndStoreId(Long userId, Long storeId);
}
