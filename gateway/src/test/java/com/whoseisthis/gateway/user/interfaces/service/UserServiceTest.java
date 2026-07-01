package com.whoseisthis.gateway.user.interfaces.service;

import com.whoseisthis.gateway.application.InternalSuccessResponse;
import com.whoseisthis.gateway.interfaces.dto.response.PaginationResponse;
import com.whoseisthis.gateway.user.interfaces.client.UserApiClient;
import com.whoseisthis.gateway.user.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.gateway.user.interfaces.dto.UserFilter;
import com.whoseisthis.gateway.user.interfaces.dto.UserResponseDto;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserApiClient userApiClient;
    @InjectMocks
    private UserService userService;

    private UpdateUserRequestDto getUpdateData()
    {
        return new UpdateUserRequestDto("budi@test.com", "new-password", "budi2");
    }

    private UserResponseDto getUsersDummy()
    {
        return new UserResponseDto(1L, "budi", "budi@test.com");
    }

    @Nested
    class GetAllUsersTest {
        @Test
        void shouldGetAllUsers()
        {
            // Arrange
            var filter = new UserFilter("budi", "budi@test.com");
            var pageable = PageRequest.of(0, 10);
            var users = List.of(getUsersDummy());
            var page = new PageImpl<>(users);
            var apiResponse = new InternalSuccessResponse<>(new PaginationResponse<>(page.stream().toList(),
                    page.getNumber(),
                    page.getSize(),
                    page.getTotalElements(),
                    page.getTotalPages()));
            when(userApiClient.getAllUsers(anyString(),
                    anyString(),
                    anyInt(),
                    anyInt(),
                    anyList())).thenReturn(Mono.just(apiResponse));

            // Act + Assert
            StepVerifier.create(userApiClient.getAllUsers(filter.name(),
                                filter.email(),
                                pageable.getPageNumber(),
                                pageable.getPageSize(),
                                pageable
                                        .getSort()
                                        .stream()
                                        .map(order -> order.getProperty() + "," + order.getDirection().name().toLowerCase())
                                        .toList()))
                        .expectNext(apiResponse)
                        .verifyComplete();
            verify(userApiClient).getAllUsers(anyString(),
                    anyString(),
                    anyInt(),
                    anyInt(),
                    anyList());
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        void shouldReturnUserByIdForAdmin()
        {
            // Arrange
            var expected = getUsersDummy();
            var apiResponse = new InternalSuccessResponse<>(expected);
            when(userApiClient.getUserByIdForAdmin(anyLong())).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(userApiClient.getUserByIdForAdmin(1L))
                        .expectNext(apiResponse)
                        .verifyComplete();
        }

        @Test
        void shouldReturnUserByIdForUser()
        {
            // Arrange
            var expected = getUsersDummy();
            var apiResponse = new InternalSuccessResponse<>(expected);
            when(userApiClient.getUserByIdForUser(anyLong())).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(userApiClient.getUserByIdForUser(1L))
                        .expectNext(apiResponse)
                        .verifyComplete();
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void shouldUpdateUserWithEmailAndPassword()
        {
            // Arrange
            var dto = getUpdateData();
            var expected = getUsersDummy();
            var apiResponse = new InternalSuccessResponse<>(expected);
            when(userApiClient.updateUser(anyLong(),
                    any(UpdateUserRequestDto.class))).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(userApiClient.updateUser(1L, dto))
                        .expectNext(apiResponse)
                        .verifyComplete();
        }
    }

    @Nested
    class ResetUserPasswordToEmail {
        @Test
        void shouldResetUserPasswordToEmail()
        {
            // Arrange
            var expected = getUsersDummy();
            var apiResponse = new InternalSuccessResponse<>(expected);
            when(userApiClient.resetUserPassword(anyLong())).thenReturn(Mono.just(apiResponse));

            // Action + Assert
            StepVerifier.create(userApiClient.resetUserPassword(1L))
                        .expectNext(apiResponse)
                        .verifyComplete();
        }
    }
}
