package com.mgbell.user.model.entity.user;

import com.mgbell.user.model.entity.store.Store;
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
    @GeneratedValue
    private Long id;
    @NotNull
    private String email;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String phoneNumber;
    @NotNull
    @Enumerated(EnumType.STRING)
    private UserRole userRole;

    @Setter
    @OneToOne(mappedBy = "user")
    private Store store;
}
