package com.mgbell.post.service;

import com.mgbell.favorite.model.entity.Favorite;
import com.mgbell.favorite.repository.FavoriteRepository;
import com.mgbell.notification.service.NotificationService;
import com.mgbell.post.exception.PostNotFoundException;
import com.mgbell.post.model.dto.request.*;
import com.mgbell.post.model.dto.response.*;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.post.repository.PostRepositoryCustom;
import com.mgbell.review.repository.ReviewRepository;
import com.mgbell.store.exception.StoreIsNotActivatedException;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.entity.Status;
import com.mgbell.store.repository.StoreImageRepository;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.exception.UserHasNoAuthorityException;
import com.mgbell.user.exception.UserHasNoPostException;
import com.mgbell.user.exception.UserHasNoStoreException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.model.entity.user.UserRole;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final PostRepositoryCustom postRepositoryCustom;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final StoreRepository storeRepository;
    private final StoreImageRepository storeImageRepository;
    private final ReviewRepository reviewRepository;
    private final NotificationService notificationService;

    @Value("${s3.url}")
    private String s3url;

    public Page<PostPreviewResponse> showAllPost(Pageable pageable,
                                                 PostPreviewRequest request,
                                                 Long userId
                                                 ) {

        Page<Post> posts = postRepositoryCustom.findByWhere(pageable, request);

        return getPostResponses(posts, userId);
    }

    public Page<PostPreviewForGuestResponse> showAllPostForGuest(Pageable pageable, PostPreviewRequest request) {
        Page<Post> posts = postRepositoryCustom.findByWhere(pageable, request);

        return getPostResponsesForGuest(posts);
    }

    public MyPostResponse getMyPost(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findByUserId(userId)
                .orElseThrow(StoreNotFoundException::new);
        Post post = postRepository.findByUserId(userId)
                .orElseThrow(PostNotFoundException::new);

        List<String> images = storeImageRepository.findByStoreId(store.getId())
                .stream()
                .map(currImage ->
                        s3url +
                                URLEncoder.encode(
                                        currImage.getOriginalFileDir(),
                                        StandardCharsets.UTF_8)
                ).toList();

        return new MyPostResponse(
                post.getPostId(),
                store.getStoreName(),
                post.getBagName(),
                post.getDescription(),
                reviewRepository.findByStoreId(store.getId()).size(),
                store.getAddress(),
                store.getLongitude(),
                store.getLatitude(),
                post.isOnSale(),
                post.getAmount(),
                post.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                post.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                post.getCostPrice(),
                post.getSalePrice(),
                images
        );
    }

    public void create(PostCreateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if(user.getUserRole() != UserRole.OWNER) throw new UserHasNoAuthorityException();

        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);

        if(store.getStatus().equals(Status.INACTIVE)) throw new StoreIsNotActivatedException();

        Post post = Post.builder()
                .user(user)
                .store(store)
                .bagName(request.getBagName())
                .description(request.getDescription())
                .costPrice(request.getCostPrice())
                .salePrice(request.getSalePrice())
                .amount(request.getAmount())
                .onSale(request.isOnSale())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .build();

        store.setPost(post);

        postRepository.save(post);
    }

    @Transactional
    public void update(PostUpdateRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        checkOwner(user);

        Post post = postRepository.findByUserId(id)
                .orElseThrow(PostNotFoundException::new);

        if(post.getAmount() != request.getAmount()) sendAmountUpdatedAlert(post.getStore());

        post.updatePost(
                request.getBagName(),
                request.getDescription(),
                request.getCostPrice(),
                request.getSalePrice(),
                request.getAmount(),
                request.isOnSale(),
                request.getStartAt(),
                request.getEndAt()
        );
    }

    @Transactional
    public void changeOnSale(OnSaleRequest request, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        checkOwner(user);

        Post post = postRepository.findByUserId(id)
                .orElseThrow(PostNotFoundException::new);

        post.setOnSale(request.isOnSale());

        if(request.isOnSale()) sendStoreOpenAlert(post.getStore());
    }

    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        checkOwner(user);

        Post post = postRepository.findByUserId(id)
                .orElseThrow(PostNotFoundException::new);

        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);

        if(!id.equals(post.getUser().getId())) {
            throw new UserHasNoAuthorityException();
        }

        store.setPost(null);
        post.setStore(null);
        postRepository.delete(post);
    }

    private Page<PostPreviewResponse> getPostResponses(Page<Post> posts, Long userId) {
        return posts.map(currPost -> {
            Store store = currPost.getStore();
            List<String> images = storeImageRepository.findByStoreId(store.getId())
                    .stream()
                    .map(currImage ->
                            s3url +
                                    URLEncoder.encode(
                                            currImage.getOriginalFileDir(),
                                            StandardCharsets.UTF_8)
                    ).toList();
            boolean favorite = favoriteRepository.existsByStoreIdAndUserId(store.getId(), userId);

            return new PostPreviewResponse(
                    currPost.getPostId(),
                    store.getStoreName(),
                    currPost.getBagName(),
                    favorite,
                    reviewRepository.findByStoreId(store.getId()).size(),
                    currPost.isOnSale(),
                    currPost.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    currPost.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    store.getAddress(),
                    store.getLongitude(),
                    store.getLatitude(),
                    currPost.getCostPrice(),
                    currPost.getSalePrice(),
                    currPost.getAmount(),
                    images);
        });
    }

    private Page<PostPreviewForGuestResponse> getPostResponsesForGuest(Page<Post> posts) {
        return posts.map(currPost -> {
            Store store = currPost.getStore();
            List<String> images = storeImageRepository.findByStoreId(store.getId())
                    .stream()
                    .map(currImage ->
                            s3url +
                                    URLEncoder.encode(
                                            currImage.getOriginalFileDir(),
                                            StandardCharsets.UTF_8)
                    ).toList();

            return new PostPreviewForGuestResponse(
                    currPost.getPostId(),
                    store.getStoreName(),
                    currPost.getBagName(),
                    reviewRepository.findByStoreId(store.getId()).size(),
                    currPost.isOnSale(),
                    currPost.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    currPost.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                    store.getAddress(),
                    store.getLongitude(),
                    store.getLatitude(),
                    currPost.getCostPrice(),
                    currPost.getSalePrice(),
                    currPost.getAmount(),
                    images);
        });
    }

    public PostResponse getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        Store store = post.getStore();

        boolean favorite = favoriteRepository.existsByStoreIdAndUserId(store.getId(), userId);

        List<String> images = storeImageRepository.findByStoreId(store.getId())
                .stream()
                .map(currImage ->
                        s3url +
                                URLEncoder.encode(
                                        currImage.getOriginalFileDir(),
                                        StandardCharsets.UTF_8)
                ).toList();

        return PostResponse.builder()
                .id(postId)
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .bagName(post.getBagName())
                .description(post.getDescription())
                .favorite(favorite)
                .reviewCnt(reviewRepository.findByStoreId(store.getId()).size())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .onSale(post.isOnSale())
                .amount(post.getAmount())
                .startAt(post.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .endAt(post.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .costPrice(post.getCostPrice())
                .salePrice(post.getSalePrice())
                .images(images)
                .build();
    }

    public PostForGuestResponse getPostForGuest(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);
        Store store = post.getStore();

        List<String> images = storeImageRepository.findByStoreId(store.getId())
                .stream()
                .map(currImage ->
                        s3url +
                                URLEncoder.encode(
                                        currImage.getOriginalFileDir(),
                                        StandardCharsets.UTF_8)
                ).toList();

        return PostForGuestResponse.builder()
                .id(postId)
                .storeId(store.getId())
                .storeName(store.getStoreName())
                .bagName(post.getBagName())
                .description(post.getDescription())
                .reviewCnt(reviewRepository.findByStoreId(store.getId()).size())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .onSale(post.isOnSale())
                .amount(post.getAmount())
                .startAt(post.getStartAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .endAt(post.getEndAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .costPrice(post.getCostPrice())
                .salePrice(post.getSalePrice())
                .images(images)
                .build();
    }

    private void checkOwner(User user) {
        if(user.getUserRole() != UserRole.OWNER) throw new UserHasNoAuthorityException();
        if(storeRepository.findByUserId(user.getId()).isEmpty()) throw new UserHasNoStoreException();
        if(postRepository.findByUserId(user.getId()).isEmpty()) throw new UserHasNoPostException();
    }

    private void sendStoreOpenAlert(Store store) {
        List<Favorite> favorites = favoriteRepository.findByStoreId(store.getId());
        List<String> userEmails = new ArrayList<>();

        favorites.forEach(currFavorite -> {
            userEmails.add(currFavorite.getUser().getEmail());
        });

        notificationService.sendOpenNotification(userEmails, store.getStoreName());
    }

    private void sendAmountUpdatedAlert(Store store) {
        List<Favorite> favorites = favoriteRepository.findByStoreId(store.getId());
        List<String> userEmails = new ArrayList<>();

        favorites.forEach(currFavorite -> {
            userEmails.add(currFavorite.getUser().getEmail());
        });

        notificationService.sendChangeNotification(userEmails, store.getStoreName());
    }
}
