package com.whoseisthis.users.interfaces.service;

import com.whoseisthis.users.application.UserUpdatedEvent;
import com.whoseisthis.users.common.exception.NotFoundError;
import com.whoseisthis.users.common.exception.UserError;
import com.whoseisthis.users.core.User;
import com.whoseisthis.users.core.UserRole;
import com.whoseisthis.users.infrastructure.PasswordService;
import com.whoseisthis.users.infrastructure.repository.UserOutboxRepository;
import com.whoseisthis.users.infrastructure.repository.UserRepository;
import com.whoseisthis.users.infrastructure.repository.UserSpecification;
import com.whoseisthis.users.interfaces.dto.SignupRequestDto;
import com.whoseisthis.users.interfaces.dto.UpdateUserRequestDto;
import com.whoseisthis.users.interfaces.dto.UserFilter;
import com.whoseisthis.users.interfaces.utils.OutboxEventsFactory;
import com.whoseisthis.users.interfaces.utils.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserOutboxRepository userOutboxRepository;
    private final PasswordService passwordService;
    private final OutboxEventsFactory outboxEventsFactory;

    public User createUser(SignupRequestDto dto)
    {
        boolean isExists = userRepository.existsByEmail(dto.email());
        if (isExists) {
            throw new UserError("Can't use this email.");
        }
        String hashedPassword = passwordService.hash(dto.password());
        User user = UserMapper.create(dto);
        user.setPassword(hashedPassword);
        User result = userRepository.save(user);
        log.info("User created successfully, userId={}", result.getId());
        return result;
    }

    public Page<User> getAllUsers(UserFilter filter, Pageable pageable)
    {
        var spec = Specification
                .where(UserSpecification.nameContains(filter.name()))
                .and(UserSpecification.emailContains(filter.email()))
                .and(UserSpecification.hasRole(UserRole.USER));
        return userRepository.findAll(spec, pageable);
    }

    @Cacheable(value = "users", key = "'user:' + #id")
    public User getUserById(Long id)
    {
        return userRepository.findById(id).orElseThrow(NotFoundError::new);
    }

    public User getUserByEmailForLogin(String email)
    {
        return userRepository.findByEmail(email).orElseThrow(() -> new UserError(
                "The email or password you entered is incorrect."));
    }

    @CacheEvict(value = "users", key = "'user:' + #id")
    @Transactional
    public User updateUser(Long id, UpdateUserRequestDto dto)
    {
        User user = getUserById(id);
        if (dto.email() != null && !dto.email().equals(user.getEmail())) {
            userRepository.findByEmail(dto.email()).ifPresent(targetUser -> {
                if (!(targetUser.getId().equals(user.getId()))) {
                    throw new UserError("Can't use this email.");
                }
            });
        }
        User updatedUser = UserMapper.update(user, dto);
        if (dto.password() != null && !dto.password().isBlank()) {
            String hashedPassword = passwordService.hash(dto.password());
            updatedUser.setPassword(hashedPassword);
        }
        OffsetDateTime now = OffsetDateTime.now();
        updatedUser.setUpdatedAt(now);
        User result = userRepository.save(updatedUser);
        var event = outboxEventsFactory.create(result.getId(),
                "user-updated",
                new UserUpdatedEvent(result.getId(), result.getName(), result.getEmail(), now));
        userOutboxRepository.save(event);
        log.info("User updated successfully, userId={}", result.getId());
        return result;
    }

    public User resetUserPasswordToEmail(Long userId)
    {
        User user = getUserById(userId);
        String hashedEmail = passwordService.hash(user.getEmail());
        user.setPassword(hashedEmail);
        userRepository.save(user);
        log.info("User password has been reset by admin, userId={}", user.getId());
        return user;
    }
}
