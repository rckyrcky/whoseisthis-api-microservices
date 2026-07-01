package com.whoseisthis.users;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class UsersApplication {
    public static void main(String[] args)
    {
        SpringApplication.run(UsersApplication.class, args);
    }

}
