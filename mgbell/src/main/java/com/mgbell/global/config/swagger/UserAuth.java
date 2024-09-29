package com.mgbell.global.config.swagger;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.annotation.Secured;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = "JWT Token")
@Secured("ROLE_USER")
public @interface UserAuth {
}
