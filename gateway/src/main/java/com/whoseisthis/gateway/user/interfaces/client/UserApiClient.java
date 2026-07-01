package com.whoseisthis.gateway.user.interfaces.client;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.user.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PatchExchange;
import org.springframework.web.service.annotation.PutExchange;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserApiClient {
    @GetExchange("/admin/users")
    Mono<InternalSuccessResponse<PaginationResponse<UserResponseDto>>> getAllUsers(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) int page,
            @RequestParam(required = false) int limit,
            @RequestParam(required = false) List<String> sort
            );

    @GetExchange("/admin/users/{id}")
    Mono<InternalSuccessResponse<UserResponseDto>> getUserByIdForAdmin(@PathVariable Long id);

    @PatchExchange("/admin/users/{id}")
    Mono<InternalSuccessResponse<UserResponseDto>> resetUserPassword(@PathVariable Long id);

    @GetExchange("/users/me/{id}")
    Mono<InternalSuccessResponse<UserResponseDto>> getUserByIdForUser(@PathVariable Long id);

    @PutExchange("/users/me/{id}")
    Mono<InternalSuccessResponse<UserResponseDto>> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserRequestDto dto);
}
