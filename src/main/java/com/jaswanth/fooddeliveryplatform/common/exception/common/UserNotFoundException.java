package com.jaswanth.fooddeliveryplatform.common.exception.common;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException(String message) {
        super(message);
    }
}