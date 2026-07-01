package com.whoseisthis.gateway.interfaces.filter;

import com.whoseisthis.gateway.application.JwtPayload;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class LoggingFilter implements WebFilter {
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
    public Mono<Void> filter(@NonNull ServerWebExchange exchange, @NonNull WebFilterChain chain)
    {
        String ip = getClientIp(exchange);
        long start = System.currentTimeMillis();

        return getUserId().flatMap(userId ->
                chain.filter(exchange).doFinally(signalType -> {
                    long duration =
                            System.currentTimeMillis() - start;

                    log.info("API request done | method={} | uri={} | status={} | ip={} | userId={} | duration={}ms",
                            exchange.getRequest().getMethod().name(),
                            exchange.getRequest().getURI().getPath(),
                            exchange.getResponse().getStatusCode(),
                            ip,
                            userId == -1L ? "anonymous" : userId,
                            duration
                            );
                }));
    }
}
