package com.jaswanth.fooddeliveryplatform.common.util;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.jaswanth.fooddeliveryplatform.common.exception.auth.InvalidPhoneNumberException;
import org.springframework.stereotype.Component;

@Component
public class PhoneNumberFormatter {

    private static final String DEFAULT_COUNTRY_REGION = "IN";
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    /**
     * Validates and normalizes phone numbers to standard E.164 (+919800000004).
     * Supports:
     * - E.164 format with leading '+' (e.g., +919800000004, +14155552671)
     * - National 10-digit numbers (e.g., 9800000004), automatically parsed with default region "IN"
     */
    public String formatToE164(String rawPhone) {
        if (rawPhone == null || rawPhone.trim().isEmpty()) {
            throw new InvalidPhoneNumberException("Phone number cannot be empty.");
        }

        String cleaned = rawPhone.trim();

        try {
            // If phone starts with '+', parse globally; otherwise fall back to "IN"
            Phonenumber.PhoneNumber parsedNumber = cleaned.startsWith("+")
                    ? phoneNumberUtil.parse(cleaned, "ZZ")
                    : phoneNumberUtil.parse(cleaned, DEFAULT_COUNTRY_REGION);

            if (!phoneNumberUtil.isValidNumber(parsedNumber)) {
                throw new InvalidPhoneNumberException("Phone number " + rawPhone + " is not valid.");
            }

            return phoneNumberUtil.format(parsedNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            throw new InvalidPhoneNumberException(
                    "Failed to parse phone number: " + rawPhone + ". Ensure valid format (e.g., +919800000004 or 9800000004)."
            );
        }
    }
}