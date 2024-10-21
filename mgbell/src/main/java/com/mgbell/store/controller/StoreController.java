package com.mgbell.store.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.AdminAuth;
import com.mgbell.global.config.swagger.AllUserAuth;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.store.model.dto.request.StoreEditRequest;
import com.mgbell.store.model.dto.request.StoreRegisterRequest;
import com.mgbell.store.model.dto.response.StoreForUserResponse;
import com.mgbell.store.model.dto.response.StoreResponse;
import com.mgbell.store.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/store")
@RequiredArgsConstructor
public class StoreController {
    private final StoreService storeService;

    @OwnerAuth
    @PostMapping(path = "/register")
    @Operation(summary = "사장님 API: 가게 등록하기")
    public void register(@RequestBody StoreRegisterRequest request, JwtAuthentication auth) {
        storeService.register(request, auth.getUserId());
    }

    @OwnerAuth
    @PatchMapping(path = "/edit")
    @Operation(summary = "사장님 API: 가게 정보 수정하기")
    public void edit(@RequestBody StoreEditRequest request, JwtAuthentication auth) {
        storeService.edit(request, auth.getUserId());
    }

    @OwnerAuth
    @GetMapping("/myStore")
    @Operation(summary = "사장님 API: 내 가게 정보 보기")
    public ResponseEntity<StoreResponse> getMyStoreInfo(JwtAuthentication auth) {
        return ResponseEntity.ok(storeService.getMyStoreInfo(auth.getUserId()));
    }

    @OwnerAuth
    @DeleteMapping
    @Operation(summary = "사장님 API: 가게 정보 삭제")
    public void delete(JwtAuthentication auth) {
        storeService.delete(auth.getUserId());
    }

    @AllUserAuth
    @GetMapping("/{storeId}")
    @Operation(summary = "일반 사용자 API: 가게 정보 보기")
    public ResponseEntity<StoreForUserResponse> getStore(@PathVariable Long storeId, JwtAuthentication auth) {
        return ResponseEntity.ok(storeService.getStore(storeId, auth.getUserId()));
    }

    @AdminAuth
    @PatchMapping(path = "/approve/{storeId}")
    @Operation(summary = "관리자 API: 가게 승인하기")
    public void approve(@PathVariable("storeId") Long storeId, JwtAuthentication auth) {
        storeService.approve(storeId, auth.getUserId());
    }

    @AdminAuth
    @GetMapping(path = "/all")
    @Operation(summary = "관리자 API: 모든 가게 보이기")
    public Page<StoreResponse> getAllStores(Pageable pageable) {
        return storeService.getAllStores(pageable);
    }

    @GetMapping(path = "/approved")
    @Operation(summary = "인증된 가게만 보이기")
    public Page<StoreResponse> getAllApprovedStores(Pageable pageable) {
        return storeService.getApprovedStore(pageable);
    }

    @AdminAuth
    @GetMapping(path = "/notApproved")
    @Operation(summary = "관리자 API: 인증되지 않은 가게만 보이기")
    public Page<StoreResponse> getNotApprovedStores(Pageable pageable) {
        return storeService.getNotApprovedStore(pageable);
    }

}
