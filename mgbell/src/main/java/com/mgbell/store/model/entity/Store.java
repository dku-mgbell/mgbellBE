package com.mgbell.store.model.entity;

import com.mgbell.global.util.BaseEntity;
import com.mgbell.post.model.entity.Post;
import com.mgbell.review.model.entity.Review;
import com.mgbell.user.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String storeName;
    @NotNull
    private String ownerName;
    @NotNull
    private String contact;
    @NotNull
    private String businessRegiNum;
    @NotNull
    private String address;
    @NotNull
    private String longitude;
    @NotNull
    private String latitude;
    @NotNull
    @Enumerated(EnumType.STRING)
    private StoreType storeType;
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    private int favorited;
    private int best;
    private int good;
    private int notBad;
    private int notGood;

    @OneToMany(mappedBy = "store", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<StoreImage> images = new ArrayList<>();

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany
    private List<Review> reviews = new ArrayList<>();

    public Store(String storeName, String ownerName, String contact, String businessRegiNum,
                 String address, String longitude, String latitude, StoreType storeType, Status status, User user) {
        this.storeName = storeName;
        this.ownerName = ownerName;
        this.contact = contact;
        this.businessRegiNum = businessRegiNum;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.storeType = storeType;
        this.status = status;
        this.user = user;
        this.favorited = 0;
    }


    public void updateStore(@NotNull String storeName, @NotNull String ownerName, @NotNull String contact,
                            @NotNull String address, String longitude, String latitude, StoreType storeType) {
        this.storeName = storeName;
        this.ownerName = ownerName;
        this.contact = contact;
        this.address = address;
        this.longitude = longitude;
        this.latitude = latitude;
        this.storeType = storeType;
        this.status = Status.INACTIVE;
    }

    public void increaseFavorited() {
        this.favorited++;
    }

    public void decreaseFavorited() {
        this.favorited--;
    }

    public void increaseBest() {
        this.best++;
    }
    public void decreaseBest() {
        this.best--;
    }
    public void increaseGood() {
        this.good++;
    }
    public void decreaseGood() {
        this.good--;
    }
    public void increaseNotBad() {
        this.notBad++;
    }
    public void decreaseNotBad() {
        this.notBad--;
    }
    public void increaseNotGood() {
        this.notGood++;
    }
    public void decreaseNotGood() {
        this.notGood--;
    }
}
