package com.whoseisthis.reports.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class AppConfig {
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer customize()
    {
        return resolver -> {
            resolver.setMaxPageSize(100);
            resolver.setSizeParameterName("limit");
        };
    }
}
