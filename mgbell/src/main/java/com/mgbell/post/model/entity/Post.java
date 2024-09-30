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
    private String title;
    @NotBlank
    private String content;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Cost cost;
    @NotNull
    private int amount;

//    @NotNull
//    private String image;

    @OneToMany(mappedBy = "post", cascade = CascadeType.PERSIST, orphanRemoval = true)
    @Builder.Default
    private List<PickupTime> pickupTime = new ArrayList<>();

    @Setter
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "store_id")
    private Store store;

    @Setter
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

}
