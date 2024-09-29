package com.mgbell.post.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PickupTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Enumerated(EnumType.STRING)
    private Week weekOfWeek;
    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void setPost(Post post) {
        if (this.post != null) {
            this.post.getPickupTime().remove(this);
        }

        this.post = post;
        this.post.getPickupTime().add(this);
    }
}
