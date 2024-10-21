package com.mgbell.favorite.repository;

import com.mgbell.favorite.model.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    boolean existsByStoreIdAndUserId(Long storeId, Long userId);
    Optional<Favorite> findByStoreIdAndUserId(Long storeId, Long userId);
    Page<Favorite> findByUserId(Pageable pageable, Long userId);
}
