package com.jaswanth.fooddeliveryplatform.common.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

    private static final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    @Override
    public boolean isValid(String phone, ConstraintValidatorContext context) {
        if (phone == null || phone.isBlank()) {
            return false;
        }

        String trimmed = phone.trim();

        // 1. Regex pre-check: optional '+' followed by 10 to 15 digits
        if (!trimmed.matches("^\\+?[1-9]\\d{9,14}$")) {
            return false;
        }

        // 2. Strict Google libphonenumber parsing (defaulting to IN if no '+' is present)
        try {
            Phonenumber.PhoneNumber parsedNumber = trimmed.startsWith("+")
                    ? phoneNumberUtil.parse(trimmed, null)
                    : phoneNumberUtil.parse(trimmed, "IN");

            return phoneNumberUtil.isValidNumber(parsedNumber);
        } catch (NumberParseException e) {
            return false;
        }
    }
}