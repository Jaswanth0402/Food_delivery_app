package com.jaswanth.fooddeliveryplatform.admin.controller;

import com.jaswanth.fooddeliveryplatform.admin.dto.CreateUserRequest;
import com.jaswanth.fooddeliveryplatform.admin.dto.CreateUserResponse;
import com.jaswanth.fooddeliveryplatform.admin.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(
            AdminUserService adminUserService
    ){
        this.adminUserService = adminUserService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateUserResponse creatUser(
            @Valid @RequestBody CreateUserRequest request
            ) {
        return adminUserService.creatUser(request);
    }

}
