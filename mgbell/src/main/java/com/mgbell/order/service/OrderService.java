package com.mgbell.order.service;

import com.mgbell.order.exception.AmountIsTooBigException;
import com.mgbell.order.exception.OrderCompleteNotAvailableException;
import com.mgbell.order.exception.OrderNotFoundException;
import com.mgbell.order.model.dto.request.OwnerOrderCancleRequest;
import com.mgbell.order.model.dto.request.UserOrderRequest;
import com.mgbell.order.model.dto.response.*;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.exception.PostNotFoundException;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public void userOrder(UserOrderRequest userOrderRequest, Long id) {
        Store store = storeRepository.findById(userOrderRequest.getStoreId())
                .orElseThrow(StoreNotFoundException::new);
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findByStore(store)
                .orElseThrow(PostNotFoundException::new);

        int leftAmount = post.getAmount() - userOrderRequest.getAmount();
        if(leftAmount < 0) {
            throw new AmountIsTooBigException();
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

        order.updateOrder(OrderState.USER_CANCELED);
    }

    @Transactional
    public void ownerAccept(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(OrderNotFoundException::new);

        if(!order.getStore().getUser().getId().equals(userId)) {
            throw new UserHasNoAuthorityException();
        }

        Post post = order.getStore().getPost();
        User user = order.getUser();

        int cnt = order.getAmount();
        int totalAmount = post.getSalePrice() * cnt;
        int carbonReduction = (post.getSalePrice() / 5900) * 2;
        int discount = (post.getCostPrice() * cnt) - totalAmount;

        user.userOrderUpdate(1, carbonReduction, discount);

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
        order.updateCancleReason(request.getCancleReason());
        Post post = order.getStore().getPost();

        int leftAmount = post.getAmount() + order.getAmount();

        post.setAmount(leftAmount);

        return new OrderRefuseResultResponse(
                order.getState(),
                order.getCancleReason().getReason()
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
                order.getCreatedAt(),
                store.getAddress(),
                order.getPayment(),
                order.getAmount(),
                order.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                order.getSubtotal(),
                order.getRequest(),
                order.getCancleReason()
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
                order.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                order.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                order.getRequest(),
                order.getAmount(),
                order.getSubtotal(),
                order.getPayment(),
                order.getCancleReason()
        );
    }

    public Page<UserOrderPreviewResponse> getUserOrderList(Pageable pageable, Long id) {
        Page<Order> order = orderRepository.findByUserId(pageable, id);

        return getUserOrderPreviewList(order);
    }

    public Page<OwnerOrderPreviewResponse> getOwnerOrderList(Pageable pageable, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Store store = user.getStore();

        Page<Order> order = orderRepository.findByStoreId(pageable, store.getId());

        return getOwnerOrderPreviewList(order);
    }

    public Page<UserOrderPreviewResponse> getUserOrderPreviewList(Page<Order> list) {
        return list.map(currOrder ->
                new UserOrderPreviewResponse(
                    currOrder.getId(),
                    currOrder.getStore().getId(),
                    currOrder.getCreatedAt(),
                    currOrder.getStore().getStoreName(),
                    currOrder.getStore().getPost().getBagName(),
                    currOrder.getState(),
                    currOrder.getAmount(),
                    currOrder.getSubtotal()
                ));
    }

    public Page<OwnerOrderPreviewResponse> getOwnerOrderPreviewList(Page<Order> list) {
        return list.map(currOrder ->
                new OwnerOrderPreviewResponse(
                        currOrder.getId(),
                        currOrder.getState(),
                        currOrder.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")),
                        currOrder.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                        currOrder.getRequest(),
                        currOrder.getAmount(),
                        currOrder.getSubtotal(),
                        currOrder.getPayment()
                ));
    }
}
