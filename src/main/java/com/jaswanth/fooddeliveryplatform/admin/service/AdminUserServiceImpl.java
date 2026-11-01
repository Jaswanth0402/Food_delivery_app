package com.jaswanth.fooddeliveryplatform.admin.service;

import com.jaswanth.fooddeliveryplatform.admin.dto.CreateUserRequest;
import com.jaswanth.fooddeliveryplatform.admin.dto.CreateUserResponse;
import com.jaswanth.fooddeliveryplatform.common.exception.auth.DuplicateEmailException;
import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


@Service
public class AdminUserServiceImpl implements AdminUserService {

    private  final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserServiceImpl(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ){

        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;

    }

    @Override
    public CreateUserResponse creatUser(CreateUserRequest request) {

        if(userRepository.existsByEmail(request.email())){
            throw new DuplicateEmailException("Email is Already register");
        }
        String hashedPassword =
                passwordEncoder.encode(request.password());

        Set<Role> roles = new HashSet<>();
        roles.add(request.role());

        User user = new User(
                request.fullName(),
                request.email(),
                hashedPassword,
                roles,
                LocalDateTime.now()
        );
        User savedUser = userRepository.save(user);

        return new CreateUserResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail(),
                savedUser.getRole()
        );


    }
}
