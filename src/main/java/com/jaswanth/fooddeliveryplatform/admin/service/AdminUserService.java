package com.jaswanth.fooddeliveryplatform.admin.service;

import com.jaswanth.fooddeliveryplatform.admin.dto.CreateUserRequest;
import com.jaswanth.fooddeliveryplatform.admin.dto.CreateUserResponse;




public interface AdminUserService {

    CreateUserResponse creatUser(CreateUserRequest request);
}
