package com.mgbell.order.repository;

import com.mgbell.order.model.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
    Page<Order> findByUserId(Pageable pageable, Long userId);
    List<Order> findByStoreId(Long storeId);
    Page<Order> findByStoreId(Pageable pageable, Long storeId);
}
