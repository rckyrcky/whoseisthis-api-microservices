package com.whoseisthis.reports.common;

import com.whoseisthis.reports.infrastructure.repository.ReportSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppSeeder implements CommandLineRunner {
    private final ReportSeeder reportSeeder;

    @Override
    public void run(String... args) throws Exception
    {
        reportSeeder.seed();
        System.out.println("Seeding is completed!");
    }
}
