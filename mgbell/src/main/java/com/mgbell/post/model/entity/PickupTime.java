package com.mgbell.post.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PickupTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Setter
    private boolean onSale;
    @NotNull
    private LocalTime startAt;
    @NotNull
    private LocalTime endAt;

    @Setter
    @OneToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "post_id")
    private Post post;

}
