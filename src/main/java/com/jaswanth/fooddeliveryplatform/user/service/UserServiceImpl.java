package com.jaswanth.fooddeliveryplatform.user.service;


import com.jaswanth.fooddeliveryplatform.common.exception.auth.DuplicatePhoneNumberException;
import com.jaswanth.fooddeliveryplatform.common.exception.common.UserNotFoundException;
import com.jaswanth.fooddeliveryplatform.common.util.PhoneNumberFormatter;
import com.jaswanth.fooddeliveryplatform.user.dto.AddRoleRequest;
import com.jaswanth.fooddeliveryplatform.user.dto.UpdateProfileRequest;
import com.jaswanth.fooddeliveryplatform.user.dto.UserResponse;
import com.jaswanth.fooddeliveryplatform.user.entity.User;
import com.jaswanth.fooddeliveryplatform.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PhoneNumberFormatter phoneNumberFormatter;

    public UserServiceImpl(UserRepository userRepository, PhoneNumberFormatter phoneNumberFormatter) {
        this.userRepository = userRepository;
        this.phoneNumberFormatter = phoneNumberFormatter;
    }

    @Override
    public UserResponse getCurrentUser(Long userId) {
        User user = findUserOrThrow(userId);
        return toResponse(user);
    }

    @Override
    public UserResponse addRole(Long userId, AddRoleRequest request) {


        User user = findUserOrThrow(userId);
        user.addRole(request.role());
        User savedUser = userRepository.save(user);
        return toResponse(savedUser);
    }
    @Override
    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = findUserOrThrow(userId);

        // Update full name
        user.setFullName(request.fullName().trim());

        // Validate and normalize phone number if provided
        if (request.phone() != null && !request.phone().trim().isEmpty()) {
            String formattedPhone = phoneNumberFormatter.formatToE164(request.phone());

            // Check if phone belongs to another user
            userRepository.findByPhone(formattedPhone).ifPresent(existingUser -> {
                if (!existingUser.getId().equals(userId)) {
                    throw new DuplicatePhoneNumberException("Phone number '" + formattedPhone + "' is already linked to another account.");
                }
            });

            user.setPhone(formattedPhone);
        }

        User updatedUser = userRepository.save(user);
        return toResponse(updatedUser);
    }

    private User findUserOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found with id: " + userId));
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isEmailVerified(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}
