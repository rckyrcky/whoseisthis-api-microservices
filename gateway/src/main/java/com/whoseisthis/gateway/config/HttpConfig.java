package com.whoseisthis.gateway.config;

import com.whoseisthis.gateway.interfaces.filter.JwtAuthenticationFilter;
import com.whoseisthis.gateway.interfaces.filter.RateLimitFilter;
import com.whoseisthis.gateway.user.core.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;

@Configuration
@RequiredArgsConstructor
public class HttpConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ServerAccessDeniedHandler customAccessDeniedHandler;
    private final ServerAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final RateLimitFilter rateLimitFilter;

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http) throws Exception
    {
        http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/login", "/auth/signup").permitAll()
                        .pathMatchers("/admin/**").hasRole(UserRole.ADMIN.name())
                        .pathMatchers("/reports/**").hasRole(UserRole.USER.name())
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui/**", "/docs/**", "/documentation/**").permitAll()
                        .anyExchange().authenticated())
                .exceptionHandling(ex -> ex
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(customAuthenticationEntryPoint))
                .addFilterBefore(
                        jwtAuthenticationFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterAfter(
                        rateLimitFilter,
                        SecurityWebFiltersOrder.AUTHENTICATION);

        return http.build();
    }
}
