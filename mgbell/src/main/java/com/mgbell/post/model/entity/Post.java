package com.mgbell.post.model.entity;

import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Post {

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
    private int amount;

//    @NotNull
//    private String image;

    @Setter
    @OneToOne(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "pickup_time_id")
    private PickupTime pickupTime;

    @Setter
    @OneToOne(mappedBy = "post", cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id")
    private Store store;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
