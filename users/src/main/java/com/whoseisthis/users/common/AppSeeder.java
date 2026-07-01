package com.whoseisthis.users.common;

import com.whoseisthis.users.infrastructure.repository.UserSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSeeder implements CommandLineRunner {
    private final UserSeeder userSeeder;

    @Override
    public void run(String... args) throws Exception
    {
        userSeeder.seed();
        System.out.println("Seeding is completed!");
    }
}
