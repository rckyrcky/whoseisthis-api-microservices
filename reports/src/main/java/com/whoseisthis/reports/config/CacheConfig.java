package com.whoseisthis.reports.config;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;

@Configuration
@Slf4j
public class CacheConfig {
    @Bean
    public RedisCacheConfiguration cacheConfiguration()
    {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(RedisSerializer.json()))
                .entryTtl(Duration.ofMinutes(5))
                .prefixCacheNameWith("c-wit-ms:")
                .disableCachingNullValues();
    }

    @Bean
    public CacheErrorHandler cacheErrorHandler()
    {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(
                    @NonNull RuntimeException exception, @NonNull Cache cache,
                    @NonNull Object key)
            {
                log.error("Cache GET error | cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCachePutError(
                    @NonNull RuntimeException exception, @NonNull Cache cache,
                    @NonNull Object key, Object value)
            {
                log.error("Cache PUT error | cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheEvictError(
                    @NonNull RuntimeException exception, @NonNull Cache cache,
                    @NonNull Object key)
            {
                log.error("Cache EVICT error | cache={}, key={}", cache.getName(), key, exception);
            }

            @Override
            public void handleCacheClearError(@NonNull RuntimeException exception, @NonNull Cache cache)
            {
                log.error("Cache CLEAR error | cache={}", cache.getName(), exception);
            }
        };
    }
}