package com.mgbell.user.model.entity.user;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
    private String userId;
    @NotNull
    private String password;
    @NotNull
    private String email;
    @NotNull
    private String name;
    @NotNull
    private String phoneNumber;
    @NotNull
    private UserRole userRole;
}
