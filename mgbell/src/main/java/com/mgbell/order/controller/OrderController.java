package com.mgbell.order.controller;

import com.mgbell.global.auth.jwt.JwtAuthentication;
import com.mgbell.global.config.swagger.OwnerAuth;
import com.mgbell.global.config.swagger.UserAuth;
import com.mgbell.order.model.dto.request.OwnerOrderCancleRequest;
import com.mgbell.order.model.dto.request.UserOrderRequest;
import com.mgbell.order.model.dto.response.*;
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
    @PostMapping("/user")
    @Operation(summary = "일반 사용자 API: 주문 하기")
    public void order(@RequestBody @Validated UserOrderRequest request, JwtAuthentication auth) {
        orderService.userOrder(request, auth.getUserId());
    }

    @UserAuth
    @PostMapping("/user/cancle/{orderId}")
    @Operation(summary = "일반 사용자 API: 주문 취소하기")
    public void userCancle(@PathVariable Long orderId, JwtAuthentication auth) {
        orderService.userCancle(orderId, auth.getUserId());
    }

    @UserAuth
    @GetMapping("/user/{orderId}")
    @Operation(summary = "일반 사용자 API: 주문 상세 내역 보기")
    public ResponseEntity<UserOrderResponse> userOrder(@PathVariable Long orderId ,JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.getUserOrder(orderId, auth.getUserId()));
    }

    @UserAuth
    @GetMapping("/user/list")
    @Operation(summary = "일반 사용자 API: 주문 내역 보기")
    public ResponseEntity<Page<UserOrderPreviewResponse>> userOrderList(Pageable pageable, JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.getUserOrderList(pageable, auth.getUserId()));
    }

    @OwnerAuth
    @PostMapping("/owner/accept/{orderId}")
    @Operation(summary = "사장님 API: 주문 수락하기")
    public void acceptOrder(@PathVariable Long orderId, JwtAuthentication auth) {
        orderService.ownerAccept(orderId, auth.getUserId());
    }

    @OwnerAuth
    @PostMapping("/owner/refuse/{orderId}")
    @Operation(summary = "사장님 API: 주문 거절하기")
    public ResponseEntity<OrderRefuseResultResponse> refuseOrder(@PathVariable Long orderId,
                                                                 @RequestBody @Validated OwnerOrderCancleRequest request,
                                                                 JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.ownerRefuse(orderId, request, auth.getUserId()));
    }

    @OwnerAuth
    @PostMapping("/owner/complete/{orderId}")
    @Operation(summary = "사장님 API: 주문 완료하기")
    public void completeOrder(@PathVariable Long orderId, JwtAuthentication auth) {
        orderService.ownerComplete(orderId, auth.getUserId());
    }

    @OwnerAuth
    @GetMapping("/owner/{orderId}")
    @Operation(summary = "사장님 API: 주문 상세 내역 보기")
    public ResponseEntity<OwnerOrderResponse> ownerOrder(@PathVariable Long orderId , JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.getOwnerOrder(orderId, auth.getUserId()));
    }

    @OwnerAuth
    @GetMapping("/owner/list")
    @Operation(summary = "사장님 API: 주문 내역 보기")
    public ResponseEntity<Page<OwnerOrderPreviewResponse>> ownerOrderList(Pageable pageable, JwtAuthentication auth) {
        return ResponseEntity.ok(orderService.getOwnerOrderList(pageable, auth.getUserId()));
    }
}
