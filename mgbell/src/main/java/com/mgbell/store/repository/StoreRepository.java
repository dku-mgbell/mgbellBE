package com.mgbell.store.repository;

import com.mgbell.store.model.entity.Status;
import com.mgbell.store.model.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {
    Page<Store> findByStatus(Status status, Pageable pageable);

    Optional<Store> findByUserId(Long id);
}
