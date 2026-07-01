package com.whoseisthis.gateway.infrastructure;

import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.distributed.AsyncBucketProxy;
import io.github.bucket4j.distributed.proxy.ProxyManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class RateLimitService {
    private final ProxyManager<String> proxyManager;
    private final String profile;

    public RateLimitService(ProxyManager<String> proxyManager, @Value("${spring.profiles.active}") String profile)
    {
        this.profile = profile;
        this.proxyManager = proxyManager;
    }

    public AsyncBucketProxy resolveBucket(String key, long token, long minutes)
    {
        BucketConfiguration configuration = BucketConfiguration.builder().addLimit(limit -> limit
                .capacity(token)
                .refillIntervally(token, Duration.ofMinutes(minutes))).build();

        return proxyManager.asAsync().getProxy(key, () -> CompletableFuture.completedFuture(configuration));
    }

    public Mono<Boolean> allow(String key, long token, long minutes)
    {
        if ("local".equals(profile)) {
            return Mono.just(true);
        }

        AsyncBucketProxy bucket = resolveBucket(key, token, minutes);
        return Mono.fromFuture(bucket.tryConsume(1))
                   .onErrorResume(ex -> {
                       log.error("Rate limit service failed | message={}", ex.getMessage(), ex);
                       return Mono.just(true);
                   });
    }
}
