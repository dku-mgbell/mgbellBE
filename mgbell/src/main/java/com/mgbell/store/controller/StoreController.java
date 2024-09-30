package com.mgbell.store.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.AdminAuth;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.store.model.dto.request.StoreEditRequest;
import com.mgbell.store.model.dto.request.StoreRegisterRequest;
import com.mgbell.store.model.dto.response.StoreResponse;
import com.mgbell.store.service.StoreService;
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
    public void register(@RequestBody StoreRegisterRequest request, JwtAuthentication auth) {
        storeService.register(request, auth.getUserId());
    }

    @OwnerAuth
    @PatchMapping(path = "/edit")
    public void edit(@RequestBody StoreEditRequest request, JwtAuthentication auth) {
        storeService.edit(request, auth.getUserId());
    }

    @OwnerAuth
    @GetMapping
    public ResponseEntity<StoreResponse> getStoreInfo(JwtAuthentication auth) {
        return ResponseEntity.ok(storeService.getStoreInfo(auth.getUserId()));
    }


    @AdminAuth
    @PatchMapping(path = "/approve/{storeId}")
    public void approve(@PathVariable("storeId") Long storeId, JwtAuthentication auth) {
        storeService.approve(storeId, auth.getUserId());
    }

    @GetMapping(path = "/all")
    public Page<StoreResponse> getAllStores(Pageable pageable) {
        return storeService.getAllStores(pageable);
    }

    @GetMapping(path = "/approved")
    public Page<StoreResponse> getAllApprovedStores(Pageable pageable) {
        return storeService.getApprovedStore(pageable);
    }

    @GetMapping(path = "/notApproved")
    public Page<StoreResponse> getNotApprovedStores(Pageable pageable) {
        return storeService.getNotApprovedStore(pageable);
    }

}
