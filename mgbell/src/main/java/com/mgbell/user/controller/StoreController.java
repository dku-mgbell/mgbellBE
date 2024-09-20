package com.mgbell.user.controller;

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

    @PostMapping(path = "/register")
    public void register(@RequestBody StoreRegisterRequest request){
        storeService.register(request);
    }

    @PatchMapping(path = "/approve/{storeId}")
    public void approve(@PathVariable("storeId") Long storeId) {
        storeService.approve(storeId);
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
