package com.mgbell.post.service;

import com.mgbell.post.model.dto.request.PickupTimeCreateRequest;
import com.mgbell.post.model.dto.request.PostCreateRequest;
import com.mgbell.post.model.dto.response.PostPreviewResponse;
import com.mgbell.post.model.entity.PickupTime;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final StoreRepository storeRepository;


    public Page<PostPreviewResponse> showAllPost(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);

        return getPostResponses(posts);
    }

    public void create(PostCreateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        Store store = user.getStore();

        Post post = Post.builder()
                .user(user)
                .store(store)
                .bagName(request.getBagName())
                .description(request.getDescription())
                .costPrice(request.getCostPrice())
                .salePrice(request.getSalePrice())
                .amount(request.getAmount())
                .build();

        store.setPost(post);
        savePickupTime(request.getPickupTime(), post);

        postRepository.save(post);
    }

    private Page<PostPreviewResponse> getPostResponses(Page<Post> posts) {
        return posts.map(currPost -> {
//            List<PostFileResponse> files = currPost.getFiles().stream()
//                    .map(file -> {
//                        String url = fileUploadService.getFileUrl(file.getFileId());
//                        return new PostFileResponse(file, url);
//                    }).collect(Collectors.toList());
            //            List<PostFileResponse> files = currPost.getFiles().stream()
//                    .map(file -> {
//                        String url = fileUploadService.getFileUrl(file.getFileId());
//                        return new PostFileResponse(file, url);
//                    }).collect(Collectors.toList());

            PickupTime currPickupTime = currPost.getPickupTime();

            return new PostPreviewResponse(
                    currPost.getPostId(),
                    currPost.getStore().getName(),
                    currPost.getBagName(),
                    currPickupTime.isOnSale(),
                    currPickupTime.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    currPickupTime.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    currPost.getCostPrice(),
                    currPost.getSalePrice(),
                    currPost.getAmount());
        });
    }

    @Transactional
    public void savePickupTime(PickupTimeCreateRequest request, Post post) {
        PickupTime pickupTime = PickupTime.builder()
                .onSale(request.isOnSale())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build();

        pickupTime.setPost(post);
        post.setPickupTime(pickupTime);

        log.info("savePickupTime!");
    }

}
