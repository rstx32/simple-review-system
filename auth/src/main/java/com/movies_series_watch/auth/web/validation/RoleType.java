package com.movies_series_watch.auth.web.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RoleTypeValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface RoleType {
    String message() default "Role must be END_USER or PRODUCT_OWNER";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}