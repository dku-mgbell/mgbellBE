package com.mgbell.store.service;

import com.mgbell.favorite.model.entity.Favorite;
import com.mgbell.favorite.repository.FavoriteRepository;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.review.model.entity.Review;
import com.mgbell.review.model.entity.ReviewImage;
import com.mgbell.review.repository.ReviewImageRepositoty;
import com.mgbell.review.repository.ReviewRepository;
import com.mgbell.store.model.dto.response.MyStoreResponse;
import com.mgbell.store.model.entity.StoreImage;
import com.mgbell.global.s3.service.S3Service;
import com.mgbell.store.exception.AlreadyHasStoreException;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.dto.response.StoreForUserResponse;
import com.mgbell.store.repository.StoreImageRepository;
import com.mgbell.user.exception.UserHasNoAuthorityException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.store.model.dto.request.StoreEditRequest;
import com.mgbell.store.model.dto.request.StoreRegisterRequest;
import com.mgbell.store.model.dto.response.StoreResponse;
import com.mgbell.store.model.entity.Status;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StoreService {
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final StoreImageRepository imageRepository;
    private final S3Service s3Service;
    private final StoreImageRepository storeImageRepository;
    private final ReviewRepository reviewRepository;
    private final PostRepository postRepository;
    private final OrderRepository orderRepository;
    private final ReviewImageRepositoty reviewImageRepositoty;

    @Value("${s3.url}")
    private String s3url;

    @Transactional
    public void register(StoreRegisterRequest request, List<MultipartFile> requestImages, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if(user.getUserRole().isUser()) throw new UserHasNoAuthorityException();
        if(storeRepository.findByUserId(id).isPresent()) throw new AlreadyHasStoreException();

        Store store = new Store(
                request.getStoreName(),
                request.getOwnerName(),
                request.getContact(),
                request.getBusinessRegiNum(),
                request.getAddress(),
                request.getLongitude(),
                request.getLatitude(),
                request.getStoreType(),
                Status.INACTIVE,
                user);

        if(requestImages != null)
            saveImages(store, requestImages);

        storeRepository.save(store);
    }

    @Transactional
    public void approve(Long storeId, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getUserRole().isAdmin()) throw new UserHasNoAuthorityException();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        store.setStatus(Status.ACTIVE);
    }

    @Transactional
    public void edit(StoreEditRequest request, List<MultipartFile> images, Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        if (!user.getUserRole().isOwner()) throw new UserHasNoAuthorityException();

        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);

//        LocalDateTime updateAt = store.getUpdatedAt();
//        if(updateAt != null && updateAt.isAfter(LocalDateTime.now().minusMinutes(5))) {
//            log.info(LocalDateTime.now().toString());
//            throw new NotEnoughTimeHasPassedException();
//        }

        store.updateStore(
                request.getStoreName(),
                request.getOwnerName(),
                request.getContact(),
                request.getAddress(),
                request.getLongitude(),
                request.getLatitude(),
                request.getStoreType()
        );

        store.setStatus(Status.INACTIVE);

        if(images == null) {
            deleteStoreImages(store);
            return;
        }
        updateImages(store, images);
    }

    @Transactional
    public void delete(Long id) {
        Store store = storeRepository.findByUserId(id)
                        .orElseThrow(StoreNotFoundException::new);

        // Todo 리뷰 있으면 삭제 안 되는 오류 수정해야함!!!!

        if(!reviewRepository.findByStoreId(id).isEmpty()) {

            reviewRepository.findByStoreId(id).forEach(currReview -> {
                deleteReviewImages(currReview);

                currReview.setUser(null);
                currReview.setOrder(null);
                currReview.setStore(null);

                reviewRepository.delete(currReview);
            });
        }

        if(!orderRepository.findByStoreId(id).isEmpty()) {
            orderRepository.findByStoreId(id).forEach(currOrder -> {
//                currOrder.setUser(null);
                currOrder.setStore(null);
                orderRepository.delete(currOrder);
            });
        }

        if(store.getPost() != null) {
            Post post = store.getPost();
            post.setStore(null);
            post.setUser(null);
            store.setPost(null);

            postRepository.delete(post);
        }

        List<Favorite> favorite = favoriteRepository.findByStoreId(store.getId());

        deleteFavorite(favorite);

        deleteStoreImages(store);

        storeRepository.delete(store);
    }

    private void deleteFavorite(List<Favorite> favorite) {
        for (Favorite favoriteItem : favorite) favoriteRepository.delete(favoriteItem);
    }

    @Transactional
    public void saveImages(Store store, List<MultipartFile> requestImages) {

        requestImages.forEach(currImage -> {
            String fileDir = store.getStoreName() + "/" + currImage.getOriginalFilename();

            s3Service.upload(currImage, fileDir);

            StoreImage image = StoreImage.builder()
                    .contentType(currImage.getContentType())
                    .fileName(currImage.getOriginalFilename())
                    .originalFileDir(fileDir)
                    .thumbnailFileDir("thumbnail/" + fileDir)
                    .store(store)
                    .build();

            imageRepository.save(image);
//            store.getImages().add(image);
        });
    }

    @Transactional
    public void updateImages(Store store, List<MultipartFile> requestImages) {
        deleteStoreImages(store);
        saveImages(store, requestImages);
    }

    @Transactional
    public void deleteReviewImages(Review review) {
        List<ReviewImage> images = review.getImages();
        for(ReviewImage image : images) {
            reviewImageRepositoty.delete(image);
            s3Service.delete(image.getOriginalFileDir());
            s3Service.delete(image.getThumbnailFileDir());
        }
    }

    @Transactional
    public void deleteStoreImages(Store store) {
        List<StoreImage> images = storeImageRepository.findByStoreId(store.getId());
        for(StoreImage image : images) {
            s3Service.delete(image.getOriginalFileDir());
            s3Service.delete(image.getThumbnailFileDir());
            imageRepository.delete(image);
        }
    }

    public StoreForUserResponse getStore(Long storeId) {

        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        List<String> images = storeImageRepository.findByStoreId(storeId).stream()
                .map(currImage ->
                        s3url +
                                URLEncoder.encode(
                                        currImage.getOriginalFileDir(),
                                        StandardCharsets.UTF_8)
                ).toList();

        return StoreForUserResponse.builder()
                .storeName(store.getStoreName())
                .businessRegiNum(store.getBusinessRegiNum())
                .reviewCnt(reviewRepository.findByStoreId(storeId).size())
                .onSale(store.getPost().isOnSale())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .startAt(store.getPost().getStartAt())
                .endAt(store.getPost().getEndAt())
                .originalFileDir(images)
                .build();
    }

    public Page<StoreResponse> getAllStores(Pageable pageable) {
        Page<Store> stores = storeRepository.findAll(pageable);

        return getStoreResponse(stores);
    }

    public Page<StoreResponse> getApprovedStore(Pageable pageable) {
        Page<Store> stores = storeRepository.findByStatus(Status.ACTIVE, pageable);
        return getStoreResponse(stores);
    }

    public Page<StoreResponse> getNotApprovedStore(Pageable pageable) {
        Page<Store> stores = storeRepository.findByStatus(Status.INACTIVE, pageable);

        return getStoreResponse(stores);
    }

    public MyStoreResponse getMyStoreInfo(Long id) {
        Store store = storeRepository.findByUserId(id)
                .orElseThrow(StoreNotFoundException::new);
        List<String> images = storeImageRepository.findByStoreId(id).stream()
                .map(currImage ->
                        s3url +
                                URLEncoder.encode(
                                        currImage.getOriginalFileDir(),
                                        StandardCharsets.UTF_8)
                ).toList();

        return MyStoreResponse.builder()
                .id(store.getId())
                .storeName(store.getStoreName())
                .ownerName(store.getOwnerName())
                .contact(store.getContact())
                .businessRegiNum(store.getBusinessRegiNum())
                .address(store.getAddress())
                .longitude(store.getLongitude())
                .latitude(store.getLatitude())
                .storeType(store.getStoreType())
                .status(store.getStatus())
                .originalFileDir(images)
                .build();
    }

    public Page<StoreResponse> getStoreResponse(Page<Store> stores) {

        return stores.map(store -> {
            List<String> images = storeImageRepository.findByStoreId(store.getId()).stream()
                    .map(currImage ->
                            s3url +
                                    URLEncoder.encode(
                                            currImage.getOriginalFileDir(),
                                            StandardCharsets.UTF_8)
                    ).toList();

            return new StoreResponse(
                    store.getId(),
                    store.getStoreName(),
                    store.getBusinessRegiNum(),
                    store.getAddress(),
                    store.getLongitude(),
                    store.getLatitude(),
                    store.getStoreType(),
                    store.getStatus(),
                    images
            );
        });
    }
}
