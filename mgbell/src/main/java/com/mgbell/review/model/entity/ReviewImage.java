package com.mgbell.review.model.entity;

import com.mgbell.global.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ReviewImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String fileName;
    @NotNull
    private String contentType;
    @NotNull
    private String originalFileDir;
    @NotNull
    private String thumbnailFileDir;

    @Setter
    @ManyToOne
    private Review review;


    public ReviewImage(String filename, String contentType,
                       String originalFileDir, String thumbnailFileDir) {
        this.fileName = filename;
        this.contentType = contentType;
        this.originalFileDir = originalFileDir;
        this.thumbnailFileDir = thumbnailFileDir;
    }
}
