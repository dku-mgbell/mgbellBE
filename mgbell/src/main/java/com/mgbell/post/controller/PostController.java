package com.mgbell.post.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.post.model.dto.request.PostCreateRequest;
import com.mgbell.post.model.dto.request.PostPreviewRequest;
import com.mgbell.post.model.dto.response.PostPreviewResponse;
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
    public ResponseEntity<Page<PostPreviewResponse>> list(Pageable pageable) {
        Page<PostPreviewResponse> posts = postService.showAllPost(pageable);

        return ResponseEntity.ok(posts);
    }

    @OwnerAuth
    @PostMapping
    @Operation(summary = "게시글 생성")
    public void create(@RequestBody @Validated PostCreateRequest request, JwtAuthentication auth) {
        postService.create(request, auth.getUserId());
    }

    @OwnerAuth
    @DeleteMapping
    public void delete(JwtAuthentication auth) {
        postService.delete(auth.getUserId());
    }

}
