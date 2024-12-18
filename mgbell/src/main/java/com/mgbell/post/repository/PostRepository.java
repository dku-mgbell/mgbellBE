package com.mgbell.post.repository;

import com.mgbell.post.model.entity.Post;
import com.mgbell.store.model.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long>{
    Optional<Post> findByUserId(Long id);
    Optional<Post> findByStore(Store store);
}
