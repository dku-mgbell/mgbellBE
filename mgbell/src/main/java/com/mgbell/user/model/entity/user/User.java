package com.mgbell.user.model.entity.user;

import com.mgbell.store.model.entity.Store;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    @Setter
    private String name;
    @NotNull
    @Setter
    private String phoneNumber;
    @NotNull
    @Setter
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Setter
    @OneToOne(mappedBy = "user", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Store store;
}
