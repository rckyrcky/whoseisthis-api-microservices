package com.whoseisthis.gateway.user.interfaces.controller;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.interfaces.dto.response.SuccessResponse;
import com.whoseisthis.gateway.user.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import com.whoseisthis.gateway.user.interfaces.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@Tag(name = "Users", description = "APIs for authenticated user operations")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @Operation(summary = "Get current user profile", description = "Retrieve the authenticated user's profile " +
            "information")
    @GetMapping("/me")
    public Mono<ResponseEntity<SuccessResponse<UserResponseDto>>> getUserById(
            @AuthenticationPrincipal JwtPayload payload)
    {
        return userService.getUserByIdForUser(payload.id()).map(result -> ResponseEntity.ok(new SuccessResponse<>(
                "Data retrieved successfully",
                result)));
    }

    @Operation(summary = "Update current user profile", description = "Update the authenticated user's profile " +
            "information")
    @PutMapping("/me")
    public Mono<ResponseEntity<SuccessResponse<UserResponseDto>>> updateUser(
            @AuthenticationPrincipal JwtPayload payload, @RequestBody @Valid UpdateUserRequestDto dto)
    {
        return userService.updateUser(payload.id(), dto).map(result -> ResponseEntity.ok(new SuccessResponse<>(
                "Data updated successfully",
                result)));
    }

}
