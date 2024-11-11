package com.mgbell.review.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.review.model.dto.request.OwnerCommentRequest;
import com.mgbell.review.model.dto.request.UserReviewEditRequest;
import com.mgbell.review.model.dto.request.UserReviewRequest;
import com.mgbell.review.model.dto.response.OwnerReviewResponse;
import com.mgbell.review.model.dto.response.ReviewResponse;
import com.mgbell.review.model.dto.response.StoreReviewPreviewResponse;
import com.mgbell.review.model.dto.response.UserReviewResponse;
import com.mgbell.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/review")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @UserAuth
    @PostMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일반 사용자 API: 리뷰 등록하기")
    public void userReview(@RequestPart @Validated UserReviewRequest request,
                           @RequestPart(required = false) List<MultipartFile> file,
                           JwtAuthentication auth) {
        reviewService.userReivew(request, file, auth.getUserId());
    }

    @UserAuth
    @GetMapping("/user/list")
    @Operation(summary = "일반 사용자 API: 내가 등록한 리뷰 보기")
    public ResponseEntity<Page<UserReviewResponse>> getUserReviewList(Pageable pageable, JwtAuthentication auth) {
        return ResponseEntity.ok(reviewService.getUserReviewList(pageable, auth.getUserId()));
    }

    @UserAuth
    @PatchMapping(path = "/user", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "일반 사용자 API: 리뷰 수정하기")
    public void userEditReview(@RequestPart @Validated UserReviewEditRequest request,
                               @RequestPart(required = false) List<MultipartFile> file,
                               JwtAuthentication auth) {
        reviewService.userEditReivew(request, file, auth.getUserId());
    }

    @UserAuth
    @DeleteMapping("/user/{reviewId}")
    @Operation(summary = "일반 사용자 API: 리뷰 삭제하기")
    public void userDeleteReview(@PathVariable Long reviewId, JwtAuthentication auth) {
        reviewService.userDeleteReivew(reviewId, auth.getUserId());
    }

    @OwnerAuth
    @PostMapping("/owner")
    @Operation(summary = "사장님 API: 리뷰에 코멘트 등록하기")
    public void ownerCommentReview(@RequestBody OwnerCommentRequest request, JwtAuthentication auth) {
        reviewService.ownerCommentReview(request, auth.getUserId());
    }

    @OwnerAuth
    @GetMapping("/owner/list")
    @Operation(summary = "사장님 API: 가게에 달린 리뷰 보기")
    public ResponseEntity<Page<OwnerReviewResponse>> getOwnerReviewList(Pageable pageable, JwtAuthentication auth) {
        return ResponseEntity.ok(reviewService.getOwnerReviewList(pageable, auth.getUserId()));
    }

    @UserAuth
    @GetMapping("/preview/{storeId}")
    @Operation(summary = "일반 사용자 API: 가게 리뷰 통계 확인하기")
    public ResponseEntity<StoreReviewPreviewResponse> getReviewPreview(@PathVariable Long storeId, JwtAuthentication auth) {
        return ResponseEntity.ok(reviewService.getReviewPreview(storeId, auth.getUserId()));
    }

    @UserAuth
    @GetMapping("/list/{storeId}")
    @Operation(summary = "일반 사용자 API: 가게 리뷰 보기")
    public ResponseEntity<Page<ReviewResponse>> getReviewList(@PathVariable Long storeId, Pageable pageable,
                                                              JwtAuthentication auth) {
        return ResponseEntity.ok(reviewService.getReviewList(pageable, storeId, auth.getUserId()));
    }
}
