package com.mgbell.order.service;

import com.mgbell.order.exception.AmountIsTooBigException;
import com.mgbell.order.exception.OrderCompleteNotAvailableException;
import com.mgbell.order.exception.OrderNotFoundException;
import com.mgbell.order.exception.PickupTimeOutOfRange;
import com.mgbell.order.model.dto.request.OwnerOrderCancleRequest;
import com.mgbell.order.model.dto.request.OwnerOrderFilterRequest;
import com.mgbell.order.model.dto.request.UserOrderRequest;
import com.mgbell.order.model.dto.response.*;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.order.repository.OrderRepositoryCustom;
import com.mgbell.post.exception.PostNotFoundException;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.review.repository.ReviewRepository;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.entity.Store;
import com.mgbell.store.repository.StoreRepository;
import com.mgbell.user.exception.UserHasNoAuthorityException;
import com.mgbell.user.exception.UserNotFoundException;
import com.mgbell.user.model.entity.user.User;
import com.mgbell.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderRepositoryCustom orderRepositoryCustom;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReviewRepository reviewRepository;
    @Value("${s3.link}")
    private String s3url;

    @Transactional
    public void userOrder(UserOrderRequest userOrderRequest, Long id) {
        Store store = storeRepository.findById(userOrderRequest.getStoreId())
                .orElseThrow(StoreNotFoundException::new);
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findByStore(store)
                .orElseThrow(PostNotFoundException::new);

        if(userOrderRequest.getPickupTime().isBefore(store.getPost().getStartAt()) ||
                userOrderRequest.getPickupTime().isAfter(store.getPost().getEndAt())) {
            throw new PickupTimeOutOfRange();
        }

        int leftAmount = post.getAmount() - userOrderRequest.getAmount();
        if(leftAmount < 0) {
            throw new AmountIsTooBigException();
        } else if(leftAmount == 0) {
            post.setOnSale(false);
        }

        post.setAmount(leftAmount);

        int cnt = userOrderRequest.getAmount();
        int totalAmount = post.getSalePrice() * cnt;

        Order order = Order.builder()
                .store(store)
                .user(user)
                .state(OrderState.REQUESTED)
                .request(userOrderRequest.getRequest())
                .pickupTime(userOrderRequest.getPickupTime())
                .amount(cnt)
                .payment(userOrderRequest.getPayment())
                .subtotal(totalAmount)
                .build();

        orderRepository.save(order);
    }

    @Transactional
    public void userCancle(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        Post post = order.getStore().getPost();

        int leftAmount = post.getAmount() + order.getAmount();

        post.setAmount(leftAmount);
        post.setOnSale(true);

        order.updateOrder(OrderState.USER_CANCELED);
    }

    @Transactional
    public void ownerAccept(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getStore().getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        User user = order.getUser();

        order.updateOrder(OrderState.ACCEPTED);
    }

    @Transactional
    public OrderRefuseResultResponse ownerRefuse(Long orderId, OwnerOrderCancleRequest request, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getStore().getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        order.updateOrder(OrderState.OWNER_REFUSED);
        order.updateCancelReason(request.getCancleReason());
        Post post = order.getStore().getPost();

        int leftAmount = post.getAmount() + order.getAmount();

        post.setAmount(leftAmount);

        return new OrderRefuseResultResponse(
                order.getState(),
                order.getCancelReason().getReason()
        );
    }

    @Transactional
    public void ownerComplete(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getStore().getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        if(order.getState() != OrderState.ACCEPTED) {
            throw new OrderCompleteNotAvailableException();
        }

        Post post = order.getStore().getPost();
        User user = order.getUser();

        int cnt = order.getAmount();
        int totalAmount = post.getSalePrice() * cnt;
        float carbonReduction = ((float) post.getSalePrice() / 5900) * 2;
        int discount = (post.getCostPrice() * cnt) - totalAmount;

        user.userOrderUpdate(1, carbonReduction, discount);

        order.updateOrder(OrderState.COMPLETED);
    }

    public UserOrderResponse getUserOrder(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        Store store = order.getStore();

        return new UserOrderResponse(
                order.getId(),
                store.getId(),
                store.getStoreName(),
                store.getPost().getBagName(),
                order.getState(),
                reviewRepository.existsByOrderId(orderId),
                order.getCreatedAt(),
                store.getAddress(),
                order.getPayment(),
                order.getAmount(),
                order.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                order.getSubtotal(),
                order.getRequest(),
                order.getCancelReason(),
        s3url + URLEncoder.encode(order.getStore().getImages().get(0).getOriginalFileDir(), StandardCharsets.UTF_8)
        );
    }

    public OwnerOrderResponse getOwnerOrder(Long orderId, Long userId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getStore().getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        Store store = order.getStore();

        return new OwnerOrderResponse(
                order.getId(),
                store.getStoreName(),
                order.getState(),
                order.getCreatedAt(),
                order.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                order.getRequest(),
                order.getUser().getPhoneNumber(),
                order.getAmount(),
                order.getSubtotal(),
                order.getPayment(),
                order.getCancelReason()
        );
    }

    public Page<UserOrderPreviewResponse> getUserOrderList(Pageable pageable, Long id) {
        Page<Order> order = orderRepository.findByUserId(pageable, id);

        return getUserOrderPreviewList(order);
    }

    public Page<OwnerOrderPreviewResponse> getOwnerOrderList(Pageable pageable,
                                                             OwnerOrderFilterRequest request,
                                                             Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Store store = user.getStore();
        Page<Order> order = orderRepositoryCustom.findByWhere(pageable, request, store.getId());

        return getOwnerOrderPreviewList(order);
    }

    public Page<UserOrderPreviewResponse> getUserOrderPreviewList(Page<Order> list) {

        return list.map(currOrder ->
                new UserOrderPreviewResponse(
                    currOrder.getId(),
                    currOrder.getStore().getPost().getPostId(),
                    currOrder.getStore().getId(),
                    currOrder.getCreatedAt(),
                    currOrder.getStore().getStoreName(),
                    currOrder.getStore().getPost().getBagName(),
                    currOrder.getState(),
                    reviewRepository.existsByOrderId(currOrder.getId()),
                    currOrder.getAmount(),
                    currOrder.getSubtotal(),
            s3url + URLEncoder.encode(currOrder.getStore().getImages().get(0).getOriginalFileDir(), StandardCharsets.UTF_8)
                ));
    }

    public Page<OwnerOrderPreviewResponse> getOwnerOrderPreviewList(Page<Order> list) {
        return list.map(currOrder ->
                new OwnerOrderPreviewResponse(
                        currOrder.getId(),
                        currOrder.getState(),
                        currOrder.getCancelReason(),
                        currOrder.getCreatedAt(),
                        currOrder.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        currOrder.getRequest(),
                        currOrder.getUser().getPhoneNumber(),
                        currOrder.getAmount(),
                        currOrder.getSubtotal(),
                        currOrder.getPayment()
                ));
    }
}
