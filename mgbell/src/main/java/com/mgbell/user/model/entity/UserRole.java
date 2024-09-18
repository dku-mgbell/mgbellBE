package com.mgbell.user.model.entity;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;

@Getter
public enum UserRole {
    GUEST("ROLE_GUEST"),
    USER("ROLE_USER"),
    OWNER("ROLE_OWNER"),
    ADMIN(combine("ROLE_ADMIN", "ROLE_USER", "ROLE_OWNER"));

    private final String name;

    UserRole(String name) {
        this.name = name;
    }

    public static String combine(String... names) {
        return String.join(",", names);
    }

    private static final Map<String, UserRole> BY_LABEL =
            Stream.of(values()).collect(Collectors.toMap(UserRole::getName, e -> e));

    public static UserRole of(String name) {
        return BY_LABEL.get(name);
    }

    public boolean isAdmin() {
        return this == ADMIN;
    }
}
