package com.mgbell.order.service;

import com.mgbell.order.exception.AmountIsTooBigException;
import com.mgbell.order.model.dto.request.OrderRequest;
import com.mgbell.order.model.dto.response.OrderResponse;
import com.mgbell.order.model.entity.Order;
import com.mgbell.order.model.entity.OrderState;
import com.mgbell.order.repository.OrderRepository;
import com.mgbell.post.exception.PostNotFoundException;
import com.mgbell.post.model.entity.Post;
import com.mgbell.post.repository.PostRepository;
import com.mgbell.store.exception.StoreNotFoundException;
import com.mgbell.store.model.entity.Store;
import com.mgbell.store.repository.StoreRepository;
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
    public void order(OrderRequest orderRequest, Long id) {
        Store store = storeRepository.findById(orderRequest.getStoreId())
                .orElseThrow(StoreNotFoundException::new);
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        Post post = postRepository.findByStore(store)
                .orElseThrow(PostNotFoundException::new);

        int leftAmount = post.getAmount() - orderRequest.getAmount();
        if(leftAmount < 0) {
            throw new AmountIsTooBigException();
        }

        post.setAmount(leftAmount);

        int cnt = orderRequest.getAmount();
        int totalAmount = post.getSalePrice() * cnt;
        int carbonReduction = cnt * 2;
        int discount = (post.getCostPrice() * cnt) - totalAmount;

        user.userOrderUpdate(1, carbonReduction, discount);

        Order order = Order.builder()
                .store(store)
                .user(user)
                .state(OrderState.REQUESTED)
                .request(orderRequest.getRequest())
                .pickupTime(orderRequest.getPickupTime())
                .amount(cnt)
                .payment(orderRequest.getPayment())
                .subtotal(totalAmount)
                .build();

        orderRepository.save(order);
    }

    public Page<OrderResponse> getOrder(Pageable pageable, Long id) {
        Page<Order> order = orderRepository.findByUserId(pageable, id);

        return getOrderList(order);
    }

    public Page<OrderResponse> getOrderList(Page<Order> list) {
        return list.map(currOrder ->
                new OrderResponse(
                    currOrder.getId(),
                    currOrder.getStore().getId(),
                    currOrder.getStore().getName(),
                    currOrder.getStore().getAddress(),
                    currOrder.getPickupTime().format(DateTimeFormatter.ofPattern("HH:mm")),
                    currOrder.getRequest(),
                    currOrder.getAmount(),
                    currOrder.getPayment()
                ));
    }
}
