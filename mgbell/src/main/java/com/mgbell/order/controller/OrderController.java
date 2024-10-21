package com.mgbell.order.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.order.model.dto.request.OrderRequest;
import com.mgbell.order.model.dto.response.OrderResponse;
import com.mgbell.order.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @UserAuth
    @PostMapping
    @Operation(summary = "일반 사용자 API: 주문 하기")
    public void order(@RequestBody @Validated OrderRequest request, JwtAuthentication auth) {
        orderService.order(request, auth.getUserId());
    }

    @UserAuth
    @GetMapping
    @Operation(summary = "일반 사용자 API: 주문 내역 보기")
    public ResponseEntity<Page<OrderResponse>> order(Pageable pageable, JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.getOrder(pageable, auth.getUserId()));
    }
}
