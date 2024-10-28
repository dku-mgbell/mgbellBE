package com.mgbell.user.model.entity.user;

import com.mgbell.order.model.entity.Order;
import com.mgbell.review.model.entity.Review;
import com.mgbell.store.model.entity.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @NotNull
    @Setter
    private String password;
    @NotNull
    @Setter
    private String name;
    @NotNull
    @Setter
    private String phoneNumber;
    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private int orderCnt;
    private int carbonReduction;
    private int totalDiscount;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Store store;

    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Order> order = new ArrayList<>();

    @Setter
    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Review> review = new ArrayList<>();

    public void userOrderUpdate(int orderCnt, int carbonReduction, int totalDiscount) {
        this.orderCnt += orderCnt;
        this.carbonReduction += carbonReduction;
        this.totalDiscount += totalDiscount;
    }
}
