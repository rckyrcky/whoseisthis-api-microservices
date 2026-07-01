package com.whoseisthis.users.infrastructure.repository;

import com.whoseisthis.users.core.User;
import com.whoseisthis.users.core.UserRole;
import com.whoseisthis.users.infrastructure.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserSeeder {
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public void seed()
    {
        if (userRepository.count() > 0) {
            System.out.println("Users is already seeded!");
            return;
        }
        String password = passwordService.hash("halo12345");
        createUser("admin@test.com", "admin", UserRole.ADMIN, password);
        createUser("john@test.com", "john", UserRole.USER, password);
        createUser("alice@test.com", "alice", UserRole.USER, password);
        for (int i = 1; i <= 200; i++) {
            createUser("user-" + i + "@test.com", "user " + i, UserRole.USER, password);
        }
        System.out.println("Users is seeded!");
    }

    private void createUser(String email, String name, UserRole role, String password)
    {
        User user = new User();
        user.setEmail(email);
        user.setName(name);
        user.setPassword(password);
        user.setRole(role);
        userRepository.save(user);
    }
}
