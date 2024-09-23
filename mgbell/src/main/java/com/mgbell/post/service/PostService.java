package com.mgbell.post.service;

import com.mgbell.post.model.dto.request.PickupTimeCreateRequest;
import com.mgbell.post.model.dto.request.PostCreateRequest;
import com.mgbell.post.model.dto.response.PostPreviewResponse;
import com.mgbell.post.model.entity.PickupTime;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.entity.store.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.StoreRepository;
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

    public void create(PostCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(UserNotFoundException::new);
        Store store = user.getStore();

        Post post = Post.builder()
                .user(user)
                .store(store)
                .title(request.getTitle())
                .content(request.getContent())
                .cost(request.getCost())
                .amount(request.getAmount())
                .build();

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

            return new PostPreviewResponse(
                    currPost.getPostId(),
                    currPost.getTitle(),
                    currPost.getStore().getName(),
                    currPost.getCost(),
                    currPost.getAmount());
        });
    }

    @Transactional
    public void savePickupTime(ArrayList<PickupTimeCreateRequest> times, Post post) {
        List<PickupTime> pickupTimes = new ArrayList<>();

        for (PickupTimeCreateRequest time : times) {
            PickupTime pickupTime = PickupTime.builder()
                    .weekOfWeek(time.getDayOfWeek())
                    .startAt(time.getStartAt())
                    .endAt(time.getEndAt())
                    .build();

            pickupTimes.add(pickupTime);
        }

        for (PickupTime time : pickupTimes) {
            time.setPost(post);
        }

        log.info("savePickupTime!");
    }

}
