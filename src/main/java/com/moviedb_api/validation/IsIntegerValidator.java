package com.moviedb_api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsIntegerValidator implements ConstraintValidator<IsInteger, String> {

    @Override
    public void initialize(IsInteger constraintAnnotation) {}

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true; // Allow null or empty values; for required fields, add @NotBlank
                         // separately
        }
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
