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
    @Setter
    private String nickname;
    @NotNull
    @Setter
    private String phoneNumber;
    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole userRole;
    private int orderCnt;
    private float carbonReduction;
    private int totalDiscount;

    public void editUserInfo(String nickname, String name, String phoneNumber) {
        this.nickname = nickname;
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    public void userOrderUpdate(int orderCnt, float carbonReduction, int totalDiscount) {
        this.orderCnt += orderCnt;
        this.carbonReduction += carbonReduction;
        this.totalDiscount += totalDiscount;
    }
}
