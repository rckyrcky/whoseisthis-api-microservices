package com.whoseisthis.users.interfaces.controller;

import com.whoseisthis.users.core.User;
import com.whoseisthis.users.interfaces.dto.PaginationResponse;
import com.whoseisthis.users.interfaces.dto.UserFilter;
import com.whoseisthis.users.interfaces.dto.UserResponseDto;
import com.whoseisthis.users.interfaces.service.UserService;
import com.whoseisthis.users.interfaces.utils.PaginationMapper;
import com.whoseisthis.users.interfaces.utils.UserMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/admin")
@Validated
@RequiredArgsConstructor
public class AdminUserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<Map<String, PaginationResponse<UserResponseDto>>> getAllUsers(
            @Valid UserFilter filter,
            Pageable pageable)
    {
        Page<User> users = userService.getAllUsers(filter, pageable);
        var result = PaginationMapper.create(users, UserMapper::get);
        return ResponseEntity.ok(Map.of("data", result));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Map<String, UserResponseDto>> getUserById(@PathVariable @Min(1) Long id)
    {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(Map.of("data", UserMapper.get(user)));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Map<String, UserResponseDto>> resetUserPassword(@PathVariable @Min(1) Long id)
    {
        User user = userService.resetUserPasswordToEmail(id);
        return ResponseEntity.ok(Map.of("data", new UserResponseDto(user.getId())));
    }
}
