package com.example.demo.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {
    private static final Logger log = LoggerFactory.getLogger(RedissonConfig.class);

    @Bean
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            config.useSingleServer()
                    .setAddress("redis://localhost:6379")
                    .setConnectionPoolSize(10)
                    .setConnectionMinimumIdleSize(5)
                    .setConnectTimeout(3000)
                    .setRetryAttempts(0);
            RedissonClient client = Redisson.create(config);
            log.info("Redisson client initialized");
            return client;
        } catch (Exception e) {
            log.warn("Redis not available, distributed locks disabled");
            return null;
        }
    }
}
