package com.expense.tracker.controller;

import com.expense.tracker.dto.UpdateUserRequest;
import com.expense.tracker.dto.UserResponse;
import com.expense.tracker.entity.User;
import com.expense.tracker.security.UserPrincipal;
import com.expense.tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/me")
    public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(userService.getCurrentUserProfile(principal.getUser()));
    }

    @PutMapping("/users/me")
    public ResponseEntity<UserResponse> updateCurrentUser(@AuthenticationPrincipal UserPrincipal principal,
                                                        @Valid @RequestBody UpdateUserRequest request) {
        User currentUser = principal.getUser();
        UserResponse updatedUser = userService.updateCurrentUser(currentUser, request.getName(), request.getEmail());

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            userService.updateCurrentUserPassword(currentUser, request.getPassword());
        }

        return ResponseEntity.ok(updatedUser);
    }
}
