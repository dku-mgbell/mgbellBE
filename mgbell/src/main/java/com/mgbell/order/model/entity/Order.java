package com.mgbell.order.model.entity;

import com.mgbell.global.util.BaseEntity;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "order_history")
public class Order extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    private OrderState state;
    private String request;
    @NotNull
    private LocalTime pickupTime;
    @NotNull
    private int amount;
    @NotNull
    private int subtotal;
    @Enumerated(EnumType.STRING)
    private Payment payment;
    @Enumerated(EnumType.STRING)
    private CancleReason cancleReason;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public void updateOrder(OrderState state) {
        this.state = state;
    }

    public void updateCancleReason(CancleReason cancleReason) {
        this.cancleReason = cancleReason;
    }
}
