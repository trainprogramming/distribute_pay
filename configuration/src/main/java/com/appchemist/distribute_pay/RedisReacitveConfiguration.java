package com.appchemist.distribute_pay;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
@RequiredArgsConstructor
public class RedisReacitveConfiguration {
    private final DistributePayConfigurationProperties properties;

    @Bean
    public ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
        return new LettuceConnectionFactory(properties.getRedisHost(), properties.getRedisPort());
    }

    @Bean
    public ReactiveRedisTemplate<String, String> redisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, RedisSerializationContext.string());
    }
}
