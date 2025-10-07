package com.movies_series_watch.auth.web.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Set;

public class RoleTypeValidator implements ConstraintValidator<RoleType, String> {
    private static final Set<String> ALLOWED = Set.of("END_USER", "PRODUCT_OWNER");

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && ALLOWED.contains(value);
    }
}