package com.mgbell.order.repository;

import com.mgbell.order.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Pageable pageable, Long userId);
    Page<Order> findByStoreId(Pageable pageable, Long storeId);
    Optional<Order> findByStoreIdAndUserId(Long storeId, Long userId);
}
