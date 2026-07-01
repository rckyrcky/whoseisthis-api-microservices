package com.whoseisthis.users.interfaces.services;

import com.whoseisthis.users.application.UserUpdatedEvent;
import com.whoseisthis.users.common.exception.NotFoundError;
import com.whoseisthis.users.common.exception.UserError;
import com.whoseisthis.users.core.User;
import com.whoseisthis.users.core.UserOutboxEvents;
import com.whoseisthis.users.core.UserRole;
import com.whoseisthis.users.infrastructure.PasswordService;
import com.whoseisthis.users.infrastructure.repository.UserOutboxRepository;
import com.whoseisthis.users.infrastructure.repository.UserRepository;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.users.interfaces.dto.UserFilter;
import com.whoseisthis.users.interfaces.service.UserService;
import com.whoseisthis.users.interfaces.utils.OutboxEventsFactory;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserOutboxRepository userOutboxRepository;
    @Mock
    private OutboxEventsFactory outboxEventsFactory;
    @Mock
    private PasswordService passwordService;
    @InjectMocks
    private UserService userService;

    private SignupRequestDto getSignupData()
    {
        return new SignupRequestDto("budi@test.com", "halo12345", "budi");
    }

    private UpdateUserRequestDto getUpdateData()
    {
        return new UpdateUserRequestDto("budi@test.com", "new-password", "budi2");
    }

    private ArgumentCaptor<User> getUserCaptor()
    {
        return ArgumentCaptor.forClass(User.class);
    }

    private User getExpectedUser(SignupRequestDto dto)
    {
        OffsetDateTime now = OffsetDateTime.now();
        return new User(1L, dto.email(), "hashed-password", dto.name(), UserRole.USER, now, now);
    }

    @Nested
    class SignupUserTest {
        @Test
        void shouldThrowErrorWhenDuplicateEmail()
        {
            // Arrange
            var dto = getSignupData();
            when(userRepository.existsByEmail(dto.email())).thenReturn(true);

            // Assert
            assertThrowsExactly(UserError.class, () -> userService.createUser(dto));
            verify(userRepository).existsByEmail(dto.email());
            verify(passwordService, never()).hash(dto.password());
            verify(userRepository, never()).save(any());
        }

        @Test
        void shouldCreateUser()
        {
            // Arrange
            var dto = getSignupData();
            var captor = getUserCaptor();
            var expected = getExpectedUser(dto);
            String hashedPassword = "hashed-password";
            when(userRepository.existsByEmail(dto.email())).thenReturn(false);
            when(passwordService.hash(dto.password())).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(expected);

            // Action
            User result = userService.createUser(dto);

            // Assert
            verify(userRepository).existsByEmail(dto.email());
            verify(passwordService).hash(dto.password());
            verify(userRepository).save(captor.capture());
            var savedUser = captor.getValue();
            assertEquals(expected.getId(), result.getId());
            assertEquals(expected.getName(), result.getName());
            assertEquals(expected.getEmail(), result.getEmail());
            assertEquals(hashedPassword, savedUser.getPassword());
        }
    }

    @Nested
    class GetAllUsersTest {
        @Test
        void shouldGetAllUsers()
        {
            // Arrange
            var filter = new UserFilter("budi", "budi@test.com");
            var pageable = PageRequest.of(0, 10);
            var users = List.of(new User(), new User());
            var page = new PageImpl<>(users);
            when(userRepository.findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable))).thenReturn(page);

            // Act
            var result = userService.getAllUsers(filter, pageable);

            // Assert
            assertEquals(2, result.getContent().size());
            verify(userRepository).findAll(ArgumentMatchers.<Specification<User>>any(), eq(pageable));
        }
    }

    @Nested
    class GetUserByIdTest {
        @Test
        void shouldReturnUserById()
        {
            // Arrange
            var expected = getExpectedUser(getSignupData());
            when(userRepository.findById(1L)).thenReturn(Optional.of(expected));

            // Action
            var result = userService.getUserById(1L);

            // Assert
            assertEquals(result, expected);
            verify(userRepository).findById(1L);
        }

        @Test
        void shouldThrowNotFoundErrorWhenNotExists()
        {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> userService.getUserById(1L));
            verify(userRepository).findById(1L);
        }
    }

    @Nested
    class GetUserByEmailForLogin {
        @Test
        void shouldReturnUserByEmail()
        {
            // Arrange
            var expected = getExpectedUser(getSignupData());
            when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.of(expected));

            // Action
            var result = userService.getUserByEmailForLogin("budi@test.com");

            // Assert
            assertEquals(result, expected);
            verify(userRepository).findByEmail("budi@test.com");
        }

        @Test
        void shouldThrowNotFoundErrorWhenNotExists()
        {
            // Arrange
            when(userRepository.findByEmail("budi@test.com")).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(UserError.class, () -> userService.getUserByEmailForLogin("budi@test.com"));
            verify(userRepository).findByEmail("budi@test.com");
        }
    }

    @Nested
    class UpdateUser {
        @Test
        void shouldThrowNotFoundErrorWhenIdNotExists()
        {
            // Arrange
            var dto = getUpdateData();
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> userService.updateUser(1L, dto));
            verify(userRepository, never()).findByEmail(dto.email());
            verify(passwordService, never()).hash(dto.password());
            verify(userRepository, never()).save(getUserCaptor().capture());
            verify(outboxEventsFactory, never()).create(anyLong(), anyString(), any(UserUpdatedEvent.class));
            verify(userOutboxRepository, never()).save(any(UserOutboxEvents.class));
        }

        @Test
        void shouldThrowUserErrorWhenEmailDuplicate()
        {
            // Arrange
            var user1 = new User();
            user1.setId(1L);
            user1.setEmail("ani@test.com");
            var user2 = new User();
            user2.setId(2L);
            user2.setEmail("budi@test.com");
            var updateUser1 = new UpdateUserRequestDto("budi@test.com", null, null);
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
            when(userRepository.findByEmail(updateUser1.email())).thenReturn(Optional.of(user2));
            verify(outboxEventsFactory, never()).create(anyLong(), anyString(), any(UserUpdatedEvent.class));
            verify(userOutboxRepository, never()).save(any(UserOutboxEvents.class));

            // Assert
            assertThrowsExactly(UserError.class, () -> userService.updateUser(1L, updateUser1));
            verify(passwordService, never()).hash(updateUser1.password());
            verify(userRepository, never()).save(getUserCaptor().capture());
        }

        @Test
        void shouldUpdateUserWithEmailAndPassword()
        {
            // Arrange
            var captor = getUserCaptor();
            var signup = getSignupData();
            var expected = getExpectedUser(signup);
            var dto = getUpdateData();
            var expectedUpdate = new User(expected.getId(), dto.email(), "new-hashed-password", dto.name(),
                    expected.getRole(), expected.getCreatedAt(), expected.getUpdatedAt());
            when(userRepository.findById(1L)).thenReturn(Optional.of(expected));
            when(userRepository.save(any(User.class))).thenReturn(expectedUpdate);
            when(passwordService.hash(dto.password())).thenReturn("new-hashed-password");
            when(outboxEventsFactory.create(anyLong(),
                    anyString(),
                    any(UserUpdatedEvent.class))).thenReturn(new UserOutboxEvents());
            when(userOutboxRepository.save(any(UserOutboxEvents.class))).thenReturn(any(UserOutboxEvents.class));

            // Action
            var result = userService.updateUser(1L, dto);

            // Assert
            verify(userRepository).findById(1L);
            verify(passwordService).hash(dto.password());
            verify(userRepository).save(captor.capture());
            verify(outboxEventsFactory).create(anyLong(), anyString(), any(UserUpdatedEvent.class));
            verify(userOutboxRepository).save(any(UserOutboxEvents.class));
            var captorValue = captor.getValue();
            assertEquals(result.getName(), expectedUpdate.getName());
            assertEquals("new-hashed-password", captorValue.getPassword());
        }

        @Test
        void shouldUpdateUserWithEmail()
        {
            // Arrange
            var dto = new UpdateUserRequestDto("budi2@test.com", null, null);
            var user1 = new User();
            user1.setId(1L);
            user1.setEmail("budi@test.com");
            var updatedUser1 = new User();
            updatedUser1.setId(1L);
            updatedUser1.setEmail("budi2@test.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser1);
            when(userRepository.findByEmail(dto.email())).thenReturn(Optional.of(updatedUser1));
            when(outboxEventsFactory.create(anyLong(),
                    anyString(),
                    any(UserUpdatedEvent.class))).thenReturn(new UserOutboxEvents());
            when(userOutboxRepository.save(any(UserOutboxEvents.class))).thenReturn(any(UserOutboxEvents.class));

            // Action
            var result = userService.updateUser(1L, dto);

            // Assert
            verify(userRepository).findById(1L);
            verify(userRepository).findByEmail(dto.email());
            verify(passwordService, never()).hash(dto.password());
            verify(userRepository).save(any(User.class));
            verify(outboxEventsFactory).create(anyLong(), anyString(), any(UserUpdatedEvent.class));
            verify(userOutboxRepository).save(any(UserOutboxEvents.class));
            assertEquals(result.getEmail(), dto.email());
        }

        @Test
        void shouldUpdateUserWithPassword()
        {
            // Arrange
            var captor = getUserCaptor();
            var dto = new UpdateUserRequestDto(null, "new-password", null);
            var user1 = new User();
            user1.setId(1L);
            user1.setEmail("budi@test.com");
            user1.setPassword("password");
            var updatedUser1 = new User();
            updatedUser1.setId(1L);
            updatedUser1.setEmail("budi2@test.com");
            updatedUser1.setPassword("new-password");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
            when(userRepository.save(any(User.class))).thenReturn(updatedUser1);
            when(passwordService.hash(dto.password())).thenReturn("new-password");
            when(outboxEventsFactory.create(anyLong(),
                    anyString(),
                    any(UserUpdatedEvent.class))).thenReturn(new UserOutboxEvents());
            when(userOutboxRepository.save(any(UserOutboxEvents.class))).thenReturn(any(UserOutboxEvents.class));

            // Action
            userService.updateUser(1L, dto);

            // Assert
            verify(userRepository).findById(1L);
            verify(passwordService).hash(dto.password());
            verify(userRepository).save(captor.capture());
            verify(outboxEventsFactory).create(anyLong(), anyString(), any(UserUpdatedEvent.class));
            verify(userOutboxRepository).save(any(UserOutboxEvents.class));
            var captorValue = captor.getValue();
            assertEquals("new-password", captorValue.getPassword());
        }

        @Test
        void shouldNotUpdatePasswordWhenPasswordIsEmpty()
        {
            // Arrange
            var dto = new UpdateUserRequestDto(null, "", null);
            var user = new User();
            user.setId(1L);
            user.setEmail("budi@test.com");
            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenReturn(user);

            // Action
            userService.updateUser(1L, dto);

            // Assert
            verify(userRepository, never()).findByEmail(dto.email());
            verify(passwordService, never()).hash(any(String.class));
        }
    }

    @Nested
    class ResetUserPasswordToEmail {
        @Test
        void shouldThrowNotFoundErrorWhenIdDoesNotExists()
        {
            // Arrange
            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            // Assert
            assertThrowsExactly(NotFoundError.class, () -> userService.resetUserPasswordToEmail(1L));
            verify(userRepository).findById(1L);
            verify(passwordService, never()).hash(anyString());
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void shouldResetUserPasswordToEmail()
        {
            // Arrange
            String email = "budi@test.com";
            String password = "old-password";
            var captor = getUserCaptor();

            var user = new User();
            user.setId(1L);
            user.setEmail(email);
            user.setPassword(password);

            var expectedUser = new User();
            expectedUser.setId(1L);
            expectedUser.setEmail(email);
            expectedUser.setPassword(email);

            when(userRepository.findById(1L)).thenReturn(Optional.of(user));
            when(passwordService.hash(anyString())).thenReturn("budi@test.com");
            when(userRepository.save(any(User.class))).thenReturn(expectedUser);

            // Action
            var result = userService.resetUserPasswordToEmail(1L);

            // Assert
            verify(userRepository).findById(1L);
            verify(passwordService).hash(email);
            verify(userRepository).save(captor.capture());
            assertEquals(email, captor.getValue().getPassword());
            assertNotEquals("old-password", result.getPassword());
        }
    }
}
