package com.mgbell.order.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.order.model.dto.request.OrderRequest;
import com.mgbell.order.model.dto.response.OrderResponse;
import com.mgbell.order.service.OrderService;
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
    public void order(@RequestBody @Validated OrderRequest request, JwtAuthentication auth) {
        orderService.order(request, auth.getUserId());
    }

    @UserAuth
    @GetMapping
    public ResponseEntity<Page<OrderResponse>> order(Pageable pageable, JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.getOrder(pageable, auth.getUserId()));
    }
}
