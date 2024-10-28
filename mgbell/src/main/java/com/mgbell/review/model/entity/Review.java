package com.mgbell.review.model.entity;

import com.mgbell.global.util.BaseEntity;
import com.mgbell.global.util.SatisfiedReasonListConverter;
import com.mgbell.store.model.entity.Store;
import com.mgbell.user.model.entity.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Review extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;
    private String ownerComent;

    @NotNull
    @Enumerated(EnumType.STRING)
    private ReviewScore reviewScore;

    @Convert(converter = SatisfiedReasonListConverter.class)
    private List<SatisfiedReason> satisfiedReasons = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;

    public Review(String content, ReviewScore reviewScore,
                  List<SatisfiedReason> satisfiedReasons, User user, Store store) {
        this.content = content;
        this.reviewScore = reviewScore;
        this.satisfiedReasons = satisfiedReasons;
        this.user = user;
        this.store = store;
    }

    public void updateReview(String content, ReviewScore reviewScore,
                        List<SatisfiedReason> satisfiedReasons) {
        this.content = content;
        this.reviewScore = reviewScore;
        this.satisfiedReasons = satisfiedReasons;
    }

    public void ownerUpdateComent(String ownerComent) {
        this.ownerComent = ownerComent;
    }
}
