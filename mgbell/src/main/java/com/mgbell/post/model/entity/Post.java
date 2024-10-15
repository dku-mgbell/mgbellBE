package com.mgbell.post.model.entity;

import com.mgbell.global.util.BaseEntity;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;
    @NotBlank
    private String bagName;
    @NotBlank
    private String description;
    @NotNull
    private int costPrice;
    @NotNull
    private int salePrice;
    @NotNull
    @Setter
    private int amount;
    @NotNull
    @Setter
    private boolean onSale;
    @NotNull
    private LocalTime startAt;
    @NotNull
    private LocalTime endAt;

//    @NotNull
//    private String image;

    @Setter
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    private Store store;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public void updatePost(String bagName, String description, int costPrice, int salePrice,
                           int amount, boolean onSale, LocalTime startAt, LocalTime endAt) {
        this.bagName = bagName;
        this.description = description;
        this.costPrice = costPrice;
        this.salePrice = salePrice;
        this.amount = amount;
        this.onSale = onSale;
        this.startAt = startAt;
        this.endAt = endAt;
    }

}
