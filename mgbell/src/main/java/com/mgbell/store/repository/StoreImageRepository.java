package com.mgbell.store.repository;

import com.mgbell.store.model.entity.StoreImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoreImageRepository extends JpaRepository<StoreImage, Long> {
    List<StoreImage> findByStoreId(Long storeId);
}
