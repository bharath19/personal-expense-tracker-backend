package com.expense.tracker.service;

import com.expense.tracker.dto.UserResponse;
import com.expense.tracker.entity.User;
import com.expense.tracker.exception.BadRequestException;
import com.expense.tracker.exception.ResourceNotFoundException;
import com.expense.tracker.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(this::mapToResponse).toList();
    }

    public UserResponse getCurrentUserProfile(User currentUser) {
        return mapToResponse(currentUser);
    }

    public UserResponse updateCurrentUser(User currentUser, String name, String email) {
        if (name != null && !name.isBlank()) {
            currentUser.setName(name.trim());
        }
        if (email != null && !email.isBlank()) {
            if (userRepository.existsByEmail(email.trim()) && !currentUser.getEmail().equalsIgnoreCase(email.trim())) {
                throw new BadRequestException("Email is already in use");
            }
            currentUser.setEmail(email.trim());
        }
        return mapToResponse(userRepository.save(currentUser));
    }

    public UserResponse updateCurrentUserPassword(User currentUser, String password) {
        if (password == null || password.length() < 6) {
            throw new BadRequestException("Password must be at least 6 characters");
        }
        currentUser.setPassword(passwordEncoder.encode(password));
        return mapToResponse(userRepository.save(currentUser));
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserResponse mapToResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.getRole(), user.getCreatedAt());
    }
}
