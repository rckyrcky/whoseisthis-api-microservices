package com.whoseisthis.gateway.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI()
    {
        return new OpenAPI().info(new Info()
                .title("WhoseIsThis API")
                .version("1.0")
                .description("REST API for Managing Lost and Found Items on Campus"));
    }
}