package com.whoseisthis.gateway.interfaces.filter;

import com.whoseisthis.gateway.application.JwtPayload;
import com.whoseisthis.gateway.common.exception.RateLimitError;
import com.whoseisthis.gateway.infrastructure.RateLimitService;
import com.whoseisthis.gateway.interfaces.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

@Component
@Slf4j
public class RateLimitFilter implements WebFilter {
    private final RateLimitService rateLimitService;
    private final ObjectMapper objectMapper;

    private final Set<String> limitedMethods = Set.of("post", "put", "patch", "delete");

    public RateLimitFilter(RateLimitService rateLimitService, ObjectMapper objectMapper)
    {
        this.rateLimitService = rateLimitService;
        this.objectMapper = objectMapper;
    }

    private String getClientIp(ServerWebExchange exchange)
    {

        String xForwardedFor = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");

        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        if (exchange.getRequest().getRemoteAddress() != null) {
            return exchange.getRequest().getRemoteAddress().getAddress().getHostAddress();
        }
        return "unknown";
    }

    private Mono<Long> getUserId()
    {
        return ReactiveSecurityContextHolder.getContext().map(ctx -> {
            Authentication auth = ctx.getAuthentication();
            if (auth == null || !auth.isAuthenticated()) {
                return -1L;
            }
            if (auth.getPrincipal() instanceof JwtPayload payload) {
                return payload.id();
            }
            return -1L;
        }).defaultIfEmpty(-1L);
    }

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, @NonNull WebFilterChain chain)
    {
        var uri = exchange.getRequest().getURI().getPath();
        var method = exchange.getRequest().getMethod().name().toLowerCase();
        var ip = getClientIp(exchange);

        return getUserId().flatMap(userId -> {
            Mono<Boolean> isAllow = Mono.defer(() -> {
                if (uri.contains("auth/login")) {
                    return rateLimitService.allow("rl-wit-ms-login::" + ip, 10, 60);
                }

                if (uri.contains("auth/signup")) {
                    return rateLimitService.allow("rl-wit-ms-signup::" + ip, 5, 60);
                }

                if (limitedMethods.contains(method)) {
                    String mutationKey = userId != -1L ? userId.toString() : ip;
                    return rateLimitService.allow("rl-wit-ms-mutation::" + mutationKey, 20, 1);
                }

                return Mono.just(true);
            });

            return isAllow.flatMap(allow -> {
                if (!allow) {
                    var err = new ErrorResponse(new RateLimitError().getMessage());

                    ServerHttpResponse response = exchange.getResponse();
                    response.setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                    response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

                    byte[] bytes = objectMapper.writeValueAsBytes(err);
                    DataBuffer buffer = response.bufferFactory().wrap(bytes);
                    log.warn("Rate limit exceeded | ip={} userId={} uri={}", ip, userId, uri);
                    return response.writeWith(Mono.just(buffer));

                } else {
                    return chain.filter(exchange);
                }
            }).onErrorResume(ex -> {
                log.error("Rate limit filter error | message={}", ex.getMessage(), ex);
                return chain.filter(exchange);
            });
        });
    }
}
