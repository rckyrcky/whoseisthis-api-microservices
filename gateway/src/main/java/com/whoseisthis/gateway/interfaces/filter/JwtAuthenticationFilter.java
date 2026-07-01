package com.whoseisthis.gateway.interfaces.filter;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.infrastructure.JwtService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final JwtService jwtService;

    private String extractTokenFromCookie(ServerWebExchange exchange)
    {
        HttpCookie cookie = exchange.getRequest().getCookies().getFirst("token");
        return cookie != null ? cookie.getValue() : null;
    }


    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain)
    {
        String token = extractTokenFromCookie(exchange);
        if (token != null) {
            JwtPayload payload = jwtService.validate(token);

            if (payload != null) {
                List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + payload
                        .role()
                        .name()));

                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(payload,
                        null,
                        authorities);

                return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth));
            }
        }

        return chain.filter(exchange);
    }
}
