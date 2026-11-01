package com.jaswanth.fooddeliveryplatform.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhoneNumber {
    String message() default "Invalid international phone number format (must include country code, e.g. +919876543210 or +14155552671)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}