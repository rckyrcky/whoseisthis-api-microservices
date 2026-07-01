package com.whoseisthis.users.interfaces.utils;

import com.whoseisthis.users.core.User;
import com.whoseisthis.users.core.UserRole;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.users.interfaces.dto.UserResponseDto;

public final class UserMapper {
    private UserMapper()
    {
    }

    public static User create(SignupRequestDto dto)
    {
        User u = new User();
        u.setEmail(dto.email().toLowerCase());
        u.setName(dto.name());
        u.setRole(UserRole.USER);
        return u;
    }

    public static User update(User user, UpdateUserRequestDto dto)
    {
        if (dto.name() != null && !dto.name().isBlank()) {
            user.setName(dto.name());
        }
        if (dto.email() != null) {
            user.setEmail(dto.email().toLowerCase());
        }
        return user;
    }

    public static UserResponseDto get(User user)
    {
        return new UserResponseDto(user.getId(), user.getName(), user.getEmail());
    }
}
