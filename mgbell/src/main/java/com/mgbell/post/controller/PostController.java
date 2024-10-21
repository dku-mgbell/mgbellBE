package com.mgbell.post.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.post.model.dto.request.OnSaleRequest;
import com.mgbell.post.model.dto.request.PostCreateRequest;
import com.mgbell.post.model.dto.request.PostPreviewRequest;
import com.mgbell.post.model.dto.request.PostUpdateRequest;
import com.mgbell.post.model.dto.response.PostPreviewResponse;
import com.mgbell.post.model.dto.response.PostResponse;
import com.mgbell.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/post")
@AllArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/list")
    @Operation(summary = "마감백 판매글 리스트")
    public ResponseEntity<Page<PostPreviewResponse>> list(PostPreviewRequest request, Pageable pageable) {

        Page<PostPreviewResponse> posts = postService.showAllPost(pageable, request);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    @Operation(summary = "판매글 상세페이지")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long postId) {
        return ResponseEntity.ok(postService.getPost(postId));
    }

    @OwnerAuth
    @PostMapping
    @Operation(summary = "사장님 API: 마감백 등록")
    public void create(@RequestBody @Validated PostCreateRequest request, JwtAuthentication auth) {
        postService.create(request, auth.getUserId());
    }

    @OwnerAuth
    @PostMapping("/onSale")
    @Operation(summary = "사장님 API: 마감백 판매 여부 on/off")
    public void onSale(@RequestBody OnSaleRequest request, JwtAuthentication auth) {
        postService.changeOnSale(request, auth.getUserId());
    }

    @OwnerAuth
    @PatchMapping
    @Operation(summary = "사장님 API: 마감백 수정")
    public void update(@RequestBody @Validated PostUpdateRequest request, JwtAuthentication auth) {
        postService.update(request, auth.getUserId());
    }

    @OwnerAuth
    @DeleteMapping
    @Operation(summary = "사장님 API: 판매글 삭제")
    public void delete(JwtAuthentication auth) {
        postService.delete(auth.getUserId());
    }

}
