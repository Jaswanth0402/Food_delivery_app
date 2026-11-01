package com.jaswanth.fooddeliveryplatform.common.exception.auth;

public class InvalidRoleRequestException extends RuntimeException {

    public InvalidRoleRequestException(String message) {
        super(message);
    }
}