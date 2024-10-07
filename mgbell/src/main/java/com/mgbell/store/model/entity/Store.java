package com.mgbell.store.model.entity;

import com.mgbell.global.util.BaseEntity;
import com.mgbell.post.model.entity.Post;
import com.mgbell.user.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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
    private String name;
    @NotNull
    private String address;
    @NotNull
    @Enumerated(EnumType.STRING)
    private StoreType storeType;
    @Setter
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status;

    @NotNull
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Setter
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "post_id")
    private Post post;

    public Store(String name, String address, StoreType storeType, Status status, User user) {
        this.name = name;
        this.address = address;
        this.storeType = storeType;
        this.status = status;
        this.user = user;
    }

    public void updateStore(String name, String address, StoreType storeType) {
        this.name = name;
        this.address = address;
        this.storeType = storeType;
    }
}
