package com.whoseisthis.users.interfaces.controller;

import com.whoseisthis.users.core.User;
import com.whoseisthis.users.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.users.interfaces.dto.UserResponseDto;
import com.whoseisthis.users.interfaces.service.UserService;
import com.whoseisthis.users.interfaces.utils.UserMapper;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/me/{id}")
    public ResponseEntity<Map<String, UserResponseDto>> getUserById(@PathVariable @Min(1) Long id)
    {
        User result = userService.getUserById(id);
        return ResponseEntity.ok(Map.of("data", UserMapper.get(result)));
    }

    @PutMapping("/me/{id}")
    public ResponseEntity<Map<String, UserResponseDto>> updateUser(
            @PathVariable @Min(1) Long id,
            @RequestBody @Valid UpdateUserRequestDto dto)
    {
        User result = userService.updateUser(id, dto);
        return ResponseEntity.ok(Map.of("data", new UserResponseDto(result.getId())));
    }
}
