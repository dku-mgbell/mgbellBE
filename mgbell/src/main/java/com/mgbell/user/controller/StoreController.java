package com.mgbell.user.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.AdminAuth;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.user.model.dto.request.StoreRegisterRequest;
import com.mgbell.user.model.dto.response.StoreResponse;
import com.mgbell.user.service.StoreService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
