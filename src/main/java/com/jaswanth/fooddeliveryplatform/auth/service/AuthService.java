package com.jaswanth.fooddeliveryplatform.auth.service;

import com.jaswanth.fooddeliveryplatform.auth.dto.LoginRequest;
import com.jaswanth.fooddeliveryplatform.auth.dto.LoginResponse;
import com.jaswanth.fooddeliveryplatform.auth.dto.RegisterRequest;
import com.jaswanth.fooddeliveryplatform.auth.dto.RegisterResponse;

public interface AuthService {

    RegisterResponse register(RegisterRequest request);

    LoginResponse login(LoginRequest request);

    void  verifyEmail(String token);
}