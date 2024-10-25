package com.mgbell.store.model.entity;

import com.mgbell.global.util.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Image extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String fileName;
    @NotNull
    private String contentType;
    @NotNull
    private String fileDir;

    @ManyToOne
    @JoinColumn(name = "store_id")
    private Store store;
}
