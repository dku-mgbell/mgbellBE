package com.mgbell.review.service;

import com.mgbell.global.s3.service.S3Service;
import com.mgbell.order.exception.OrderNotFoundException;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.review.exception.EditNotAvailableException;
import com.mgbell.review.exception.ReviewNotAvailableException;
import com.mgbell.review.exception.ReviewNotFoundException;
import com.mgbell.review.model.dto.request.OwnerCommentRequest;
import com.mgbell.review.model.dto.request.ReviewFilterRequest;
import com.mgbell.review.model.dto.request.UserReviewEditRequest;
import com.mgbell.review.model.dto.request.UserReviewRequest;
import com.mgbell.review.model.dto.response.*;
import com.mgbell.review.model.entity.Review;
import com.mgbell.review.model.entity.ReviewImage;
import com.mgbell.review.model.entity.ReviewScore;
import com.mgbell.review.repository.ReviewImageRepositoty;
import com.mgbell.review.repository.ReviewRepository;
import com.mgbell.review.repository.ReviewRepositoryCustom;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.entity.Store;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.exception.UserHasNoAuthorityException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.entity.user.User;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewRepositoryCustom reviewRepositoryCustom;
    private final UserRepository userRepository;
    private final ReviewImageRepositoty reviewImageRepositoty;
    private final StoreRepository storeRepository;
    private final OrderRepository orderRepository;
    private final S3Service s3Service;

    @Value("${s3.url}")
    private String s3url;

    @Transactional
    public void userReivew(UserReviewRequest request, List<MultipartFile> requestImages, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(OrderNotFoundException::new);
        Store store = order.getStore();

        if(!user.getUserRole().isUser() || order.getUser() != user) throw new UserHasNoAuthorityException();

        if(order.getState() != OrderState.COMPLETED
//                || order.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(10))
        ) {
            throw new ReviewNotAvailableException();
        }

        Review review = new Review(
                request.getContent(),
                request.getReviewScore(),
                request.getSatisfiedReasons(),
                user,
                order.getStore(),
                order
                );

        store.getReviews().add(review);
        increaseReviewScore(order.getStore(), request.getReviewScore());

        if(requestImages != null)
            saveImages(review, requestImages);

        reviewRepository.save(review);
    }


    @Transactional
    public void userEditReivew(UserReviewEditRequest request, List<MultipartFile> file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(!user.getUserRole().isUser()) throw new UserHasNoAuthorityException();

        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(EditNotAvailableException::new);

        Store store = review.getStore();

        if(LocalDateTime.now().isAfter(review.getCreatedAt().plusDays(3))) {
            throw new ReviewNotAvailableException();
        }

        decreaseReviewScore(store, request.getReviewScore());

        review.updateReview(
                request.getContent(),
                request.getReviewScore(),
                request.getSatisfiedReasons()
        );

        updateImages(review, file);

        increaseReviewScore(store, request.getReviewScore());
    }

    @Transactional
    public void userDeleteReivew(Long reviewId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(!user.getUserRole().isUser()) throw new UserHasNoAuthorityException();

        Review review = reviewRepository.findByIdAndUserId(reviewId, userId)
                .orElseThrow(ReviewNotFoundException::new);

        decreaseReviewScore(review.getStore(), review.getReviewScore());
        review.setUser(null);
        review.setOrder(null);
        review.getStore().getReviews().remove(review);
        reviewRepository.delete(review);
    }

    @Transactional
    public void ownerCommentReview(OwnerCommentRequest request, Long userId) {

        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(ReviewNotFoundException::new);

        if(!Objects.equals(review.getStore().getUser().getId(), userId)) throw new UserHasNoAuthorityException();

        review.ownerUpdateComent(request.getComment(), LocalDateTime.now());
    }

    public Page<OwnerReviewResponse> getOwnerReviewList(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<Review> review = reviewRepository.findByStoreId(pageable, user.getStore().getId());

        return getOwnerReviewPage(review);
    }

    public Page<OwnerReviewResponse> getOwnerReviewPage(Page<Review> reviews) {
        return reviews.map(currReview -> {
            List<String> images = currReview.getImages().stream()
                    .map(currImage -> s3url + URLEncoder.encode(currImage.getOriginalFileDir(), StandardCharsets.UTF_8)).toList();

            return new OwnerReviewResponse(
                    currReview.getId(),
                    currReview.getUser().getName(),
                    currReview.getContent(),
                    currReview.getOwnerComent(),
                    currReview.getOwnerCommentDate(),
                    currReview.getSatisfiedReasons(),
                    images
            );
        });
    }

    public Page<UserReviewResponse> getUserReviewList(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Page<Review> review = reviewRepository.findByUserId(pageable, user.getId());

        return getUserReviewPage(review);
    }

    public Page<UserReviewResponse> getUserReviewPage(Page<Review> reviews) {
        return reviews.map(currReview -> {
            List<String> images = currReview.getImages().stream()
                    .map(currImage -> s3url + URLEncoder.encode(currImage.getOriginalFileDir(), StandardCharsets.UTF_8)).toList();

            Store store = currReview.getStore();

            return new UserReviewResponse(
                    store.getPost().getPostId(),
                    store.getId(),
                    currReview.getId(),
                    currReview.getCreatedAt(),
                    store.getStoreName(),
                    currReview.getReviewScore(),
                    currReview.getContent(),
                    currReview.getOwnerComent(),
                    currReview.getOwnerCommentDate(),
                    currReview.getSatisfiedReasons(),
                    images
            );
        });
    }

    public StoreReviewPreviewResponse getReviewPreview(Long storeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        ReviewScore mostReviewScore = getMostReviewScore(store);

        return new StoreReviewPreviewResponse(
                mostReviewScore,
                new ReviewCountsResponse(
                        store.getBest(),
                        store.getGood(),
                        store.getNotBad(),
                        store.getNotGood()
                ),
                store.getReviews().size()
        );
    }

    private static ReviewScore getMostReviewScore(Store store) {
        List<Integer> reviewScoreCnts = new ArrayList<>();
        reviewScoreCnts.add(store.getBest());
        reviewScoreCnts.add(store.getGood());
        reviewScoreCnts.add(store.getNotGood());
        reviewScoreCnts.add(store.getNotBad());

        int maxReviewScore = Collections.max(reviewScoreCnts);

        if(maxReviewScore == 0){
            return ReviewScore.NONE;
        }

        return
                switch (reviewScoreCnts.indexOf(maxReviewScore)) {
                    case 1 -> ReviewScore.GOOD;
                    case 2 -> ReviewScore.NOTGOOD;
                    case 3 -> ReviewScore.NOTBAD;
                    default -> ReviewScore.BEST;
                };
    }

    public Page<ReviewResponse> getReviewList(ReviewFilterRequest request, Pageable pageable, Long storeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        Page<Review> reviews = reviewRepositoryCustom.findByWhere(pageable, request, storeId);

        return getReviewPage(reviews);
    }

    public Page<ReviewResponse> getReviewPage(Page<Review> reviews) {
        return reviews.map(currReview -> {
            if (currReview.getUser() == null) {
                return new ReviewResponse(
                        "탈퇴한 사용자",
                        currReview.getCreatedAt(),
                        currReview.getReviewScore(),
                        currReview.getContent(),
                        currReview.getSatisfiedReasons(),
                        currReview.getImages().stream().map(currImage ->
                                s3url + URLEncoder.encode(currImage.getOriginalFileDir(), StandardCharsets.UTF_8)).toList(),
                        currReview.getOwnerComent(),
                        currReview.getOwnerCommentDate()
                );
            }

            return new ReviewResponse(
                    currReview.getUser().getName(),
                    currReview.getCreatedAt(),
                    currReview.getReviewScore(),
                    currReview.getContent(),
                    currReview.getSatisfiedReasons(),
                    currReview.getImages().stream().map(currImage ->
                            s3url + URLEncoder.encode(currImage.getOriginalFileDir(), StandardCharsets.UTF_8)).toList(),
                    currReview.getOwnerComent(),
                    currReview.getOwnerCommentDate()
            );
        });
    }

    @Transactional
    public void saveImages(Review review, List<MultipartFile> requestImages) {

        requestImages.forEach(currImage -> {
            String fileDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + "/" + UUID.randomUUID();

            s3Service.upload(currImage, fileDir);

            ReviewImage image = ReviewImage.builder()
                    .contentType(currImage.getContentType())
                    .fileName(currImage.getOriginalFilename())
                    .originalFileDir(fileDir)
                    .thumbnailFileDir("thumbnail/" + fileDir)
                    .review(review)
                    .build();

            reviewImageRepositoty.save(image);
            review.getImages().add(image);
        });
    }

    @Transactional
    public void updateImages(Review review, List<MultipartFile> requestImages) {
        List<ReviewImage> images = review.getImages();
        for(ReviewImage image : images) {
            reviewImageRepositoty.delete(image);
            s3Service.delete(image.getOriginalFileDir());
            s3Service.delete(image.getThumbnailFileDir());
        }

        saveImages(review, requestImages);
    }

    @Transactional
    public void increaseReviewScore(Store store, ReviewScore reviewScore) {
        switch (reviewScore) {
            case BEST:
                store.increaseBest();
                break;
            case GOOD:
                store.increaseGood();
                break;
            case NOTBAD:
                store.increaseNotBad();
                break;
            case NOTGOOD:
                store.increaseNotGood();
                break;
        }
    }

    @Transactional
    public void decreaseReviewScore(Store store, ReviewScore reviewScore) {
        switch (reviewScore) {
            case BEST:
                store.decreaseBest();
                break;
            case GOOD:
                store.decreaseGood();
                break;
            case NOTBAD:
                store.decreaseNotBad();
                break;
            case NOTGOOD:
                store.decreaseNotGood();
                break;
        }
    }
}
