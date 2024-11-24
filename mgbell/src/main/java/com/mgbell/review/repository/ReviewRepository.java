package com.mgbell.review.repository;

import com.mgbell.review.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUserId(Long userId);
    Page<Review> findByUserId(Pageable pageable, Long userId);
    List<Review> findByStoreId(Long storeId);
    Page<Review> findByStoreId(Pageable pageable, Long storeId);
    Optional<Review> findByIdAndUserId(Long reviewId, Long userId);
    boolean existsByOrderId(Long orderId);
}
