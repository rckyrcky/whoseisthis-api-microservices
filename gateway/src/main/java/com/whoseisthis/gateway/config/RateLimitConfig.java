package com.whoseisthis.gateway.config;

import io.github.bucket4j.distributed.proxy.ProxyManager;
import io.github.bucket4j.redis.lettuce.Bucket4jLettuce;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.codec.ByteArrayCodec;
import io.lettuce.core.codec.RedisCodec;
import io.lettuce.core.codec.StringCodec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RateLimitConfig {
    @Bean
    public ProxyManager<String> proxyManager(LettuceConnectionFactory connectionFactory)
    {
        var nativeClient = connectionFactory.getNativeClient();

        if (nativeClient instanceof RedisClient redisClient) {
            StatefulRedisConnection<String, byte[]> connection = redisClient.connect(RedisCodec.of(StringCodec.UTF8,
                    ByteArrayCodec.INSTANCE));

            return Bucket4jLettuce.casBasedBuilder(connection).build();
        }

        throw new IllegalStateException("Unsupported Redis client type");
    }
}
