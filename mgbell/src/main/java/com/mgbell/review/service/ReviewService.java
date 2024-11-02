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
import com.mgbell.review.model.dto.request.UserReviewRequest;
import com.mgbell.review.model.dto.response.OwnerReviewResponse;
import com.mgbell.review.model.dto.response.ReviewResponse;
import com.mgbell.review.model.dto.response.StoreReviewPreviewResponse;
import com.mgbell.review.model.dto.response.UserReviewResponse;
import com.mgbell.review.model.entity.Review;
import com.mgbell.review.model.entity.ReviewImage;
import com.mgbell.review.model.entity.ReviewScore;
import com.mgbell.review.model.entity.SatisfiedReason;
import com.mgbell.review.repository.ReviewImageRepositoty;
import com.mgbell.review.repository.ReviewRepository;
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
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(StoreNotFoundException::new);
        Order order = orderRepository.findByStoreIdAndUserId(store.getId(), user.getId())
                .orElseThrow(OrderNotFoundException::new);

        if(!user.getUserRole().isUser()) throw new UserHasNoAuthorityException();

        if(order.getState() != OrderState.COMPLETED || order.getUpdatedAt().isAfter(LocalDateTime.now().minusMinutes(10))) {
            throw new ReviewNotAvailableException();
        }

        Review review = new Review(
                request.getContent(),
                request.getReviewScore(),
                request.getSatisfiedReasons(),
                user,
                store);

        increaseReviewScore(store, request.getReviewScore());

        if(requestImages != null)
            saveImages(review, requestImages);

        reviewRepository.save(review);
    }


    @Transactional
    public void userEditReivew(UserReviewRequest request, List<MultipartFile> file, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(StoreNotFoundException::new);

        if(!user.getUserRole().isUser()) throw new UserHasNoAuthorityException();

        Review review = reviewRepository.findByUserIdAndStoreId(user.getId(), store.getId())
                .orElseThrow(EditNotAvailableException::new);

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
    public void userDeleteReivew(Long storeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        if(!user.getUserRole().isUser()) throw new UserHasNoAuthorityException();

        Review review = reviewRepository.findByUserIdAndStoreId(user.getId(), store.getId())
                .orElseThrow(ReviewNotFoundException::new);

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
//            List<String> images = currReview.getImages().stream().map(ReviewImage::getOriginalFileDir).toList();
            List<String> images = currReview.getImages().stream()
                    .map(currImage -> s3url + URLEncoder.encode(currImage.getOriginalFileDir(), StandardCharsets.UTF_8)).toList();

            return new UserReviewResponse(
                    currReview.getStore().getId(),
                    currReview.getId(),
                    currReview.getCreatedAt(),
                    currReview.getStore().getStoreName(),
                    currReview.getReviewScore(),
                    currReview.getContent(),
                    currReview.getOwnerComent(),
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

        List<Integer> reviewScoreCnts = new ArrayList<>();
        reviewScoreCnts.add(store.getBest());
        reviewScoreCnts.add(store.getGood());
        reviewScoreCnts.add(store.getNotGood());
        reviewScoreCnts.add(store.getNotBad());

        int maxReviewScore = Collections.max(reviewScoreCnts);

        ReviewScore reviewScore = switch (reviewScoreCnts.indexOf(maxReviewScore)) {
            case 1 -> ReviewScore.GOOD;
            case 2 -> ReviewScore.NOTGOOD;
            case 3 -> ReviewScore.NOTBAD;
            default -> ReviewScore.BEST;
        };

        return new StoreReviewPreviewResponse(
                reviewScore,
                store.getBest(),
                store.getGood(),
                store.getNotGood(),
                store.getNotBad(),
                store.getReviews().size()
        );
    }

    public Page<ReviewResponse> getReviewList(Pageable pageable, Long storeId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        Store store = storeRepository.findById(storeId)
                .orElseThrow(StoreNotFoundException::new);

        Page<Review> reviews = reviewRepository.findByStoreId(pageable, store.getId());

        return getReviewPage(reviews);
    }

    public Page<ReviewResponse> getReviewPage(Page<Review> reviews) {
        return reviews.map(currReview ->
                new ReviewResponse(
                        currReview.getUser().getName(),
                        currReview.getCreatedAt(),
                        currReview.getReviewScore(),
                        currReview.getContent(),
                        currReview.getSatisfiedReasons(),
                        currReview.getImages().stream().map(currImage ->
                                s3url + URLEncoder.encode(currImage.getOriginalFileDir(), StandardCharsets.UTF_8)).toList(),
                        currReview.getOwnerComent()
                ));
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
