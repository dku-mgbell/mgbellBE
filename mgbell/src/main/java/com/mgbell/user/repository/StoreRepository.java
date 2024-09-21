package com.mgbell.user.repository;

import com.mgbell.user.model.entity.store.Status;
import com.mgbell.user.model.entity.store.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<Store, Long> {
    Page<Store> findByStatus(Status status, Pageable pageable);
}
