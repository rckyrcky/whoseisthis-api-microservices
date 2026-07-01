package com.whoseisthis.gateway.user.interfaces.service;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.user.interfaces.client.UserApiClient;
import com.whoseisthis.gateway.user.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.gateway.user.interfaces.dto.UserFilter;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserApiClient userApiClient;

    public Mono<PaginationResponse<UserResponseDto>> getAllUsers(UserFilter filter, Pageable pageable)
    {
        return userApiClient.getAllUsers(
                filter.name(),
                filter.email(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getSort().stream().map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase()).toList()).map(
                InternalSuccessResponse::data);
    }

    public Mono<UserResponseDto> getUserByIdForAdmin(Long id)
    {
        return userApiClient.getUserByIdForAdmin(id).map(InternalSuccessResponse::data);
    }

    public Mono<UserResponseDto> getUserByIdForUser(Long id)
    {
        return userApiClient.getUserByIdForUser(id).map(InternalSuccessResponse::data);
    }

    public Mono<UserResponseDto> updateUser(Long id, UpdateUserRequestDto dto)
    {
        return userApiClient.updateUser(id, dto).map(InternalSuccessResponse::data);
    }

    public Mono<UserResponseDto> resetUserPasswordToEmail(Long userId)
    {
        return userApiClient.resetUserPassword(userId).map(InternalSuccessResponse::data);
    }
}
