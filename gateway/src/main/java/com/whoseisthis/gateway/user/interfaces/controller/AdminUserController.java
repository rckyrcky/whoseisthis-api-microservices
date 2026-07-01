package com.whoseisthis.gateway.user.interfaces.controller;

import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.interfaces.dto.response.SuccessResponse;
import com.whoseisthis.gateway.user.interfaces.dto.UserFilter;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import com.whoseisthis.gateway.user.interfaces.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Admin - Users", description = "Admin APIs for managing users")
@RestController()
@RequestMapping("/admin")
@Validated
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @Operation(summary = "Get all users", description = "Retrieve a paginated list of users with optional filtering")
    @GetMapping("/users")
    public Mono<ResponseEntity<SuccessResponse<PaginationResponse<UserResponseDto>>>> getAllUsers(
            @Valid UserFilter filter, Pageable pageable)
    {
        return userService.getAllUsers(filter, pageable).map(result -> ResponseEntity.ok(new SuccessResponse<>(
                "Data retrieved successfully",
                result)));
    }

    @Operation(summary = "Get user by ID", description = "Retrieve a user's details by their ID")
    @GetMapping("/users/{id}")
    public Mono<ResponseEntity<SuccessResponse<UserResponseDto>>> getUserById(@PathVariable @Min(1) Long id)
    {
        return userService
                .getUserByIdForAdmin(id)
                .map(result -> ResponseEntity.ok(new SuccessResponse<>("Data retrieved successfully", result)));
    }

    @Operation(summary = "Reset user password by ID", description = "Reset user password to their email")
    @PatchMapping("/users/{id}")
    public Mono<ResponseEntity<SuccessResponse<UserResponseDto>>> resetUserPassword(@PathVariable @Min(1) Long id)
    {
        return userService.resetUserPasswordToEmail(id).map(result -> ResponseEntity.ok(new SuccessResponse<>(
                "Password reset successfully",
                result)));
    }
}
