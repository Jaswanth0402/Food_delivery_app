package com.jaswanth.fooddeliveryplatform.auth.service;


import com.jaswanth.fooddeliveryplatform.auth.dto.LoginRequest;
import com.jaswanth.fooddeliveryplatform.auth.dto.LoginResponse;
import com.jaswanth.fooddeliveryplatform.auth.dto.RegisterRequest;
import com.jaswanth.fooddeliveryplatform.auth.dto.RegisterResponse;
import com.jaswanth.fooddeliveryplatform.common.exception.auth.DuplicateEmailException;
import com.jaswanth.fooddeliveryplatform.common.exception.auth.EmailNotVerifiedException;
import com.jaswanth.fooddeliveryplatform.common.exception.auth.InvalidCredentialsException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.ResourceNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.util.PhoneNumberFormatter;
import com.jaswanth.fooddeliveryplatform.security.JwtService;
import com.jaswanth.fooddeliveryplatform.user.entity.Role;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthServiceImpl implements  AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final PhoneNumberFormatter phoneNumberFormatter;

    public AuthServiceImpl(UserRepository userRepository,PhoneNumberFormatter phoneNumberFormatter, PasswordEncoder passwordEncoder,JwtService jwtService){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.phoneNumberFormatter = phoneNumberFormatter;
    }

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        // 1. Canonicalize email
        String normalizedEmail = request.email().trim().toLowerCase();

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new DuplicateEmailException("Email '" + normalizedEmail + "' is already registered.");
        }

        // 2. Validate and format phone number into E.164 (+1..., +91...)
        String formattedPhone = phoneNumberFormatter.formatToE164(request.phone());

        // 3. Hash password
        String hashedPassword = passwordEncoder.encode(request.password());

        // 4. Default role assignment
        Set<Role> roles = new HashSet<>();
        roles.add(Role.CUSTOMER);

        // 5. Generate secure email verification token
        String verificationToken = UUID.randomUUID().toString();

        User user = new User(
                request.fullName().trim(),
                normalizedEmail,
                hashedPassword,
                roles,
                LocalDateTime.now()
        );
        user.setPhone(formattedPhone);
        user.setEmailVerified(false); // Verification required
        user.setVerificationToken(verificationToken);

        User savedUser = userRepository.save(user);

        // In a real mail flow, send verification email with token here:
        // emailService.sendVerificationEmail(savedUser.getEmail(), verificationToken);

        return new RegisterResponse(
                savedUser.getId(),
                savedUser.getFullName(),
                savedUser.getEmail()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();

        // Prevent timing attacks by checking password hash even if not found (or return unified message)
        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password.");
        }

        // Enforce verified email constraint
        if (!user.isEmailVerified()) {
            throw new EmailNotVerifiedException("Please verify your email address before logging in. A verification link was sent to: " + user.getEmail());
        }

        String token = jwtService.generateToken(user);
        return new LoginResponse(token);
    }

    @Transactional
    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Invalid or expired verification token."));

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        userRepository.save(user);
    }
}
