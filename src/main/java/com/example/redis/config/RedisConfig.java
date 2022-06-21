package com.example.redis.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        // 设置连接池
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置序列化方式
        GenericJackson2JsonRedisSerializer jsonRedisSerializer =
                new GenericJackson2JsonRedisSerializer();

        // key和 hashKey采用 string序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setHashKeySerializer(RedisSerializer.string());
        // value和 hashValue采用 JSON序列化
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        return redisTemplate;
    }

    @Bean(name = "redissonClient1")
    public RedissonClient redissonClient1() {
        Config config = new Config();
//        config.useClusterServers();
        config.useSingleServer().setAddress("redis://localhost:6379");
        return Redisson.create(config);
    }

//    @Bean(name = "redissonClient2")
//    public RedissonClient redissonClient2() {
//        Config config = new Config();
//        config.useClusterServers();
//        config.useSingleServer().setAddress("redis://localhost:6379");
//        return Redisson.create(config);
//    }
//
//    @Bean(name = "redissonClient3")
//    public RedissonClient redissonClient3() {
//        Config config = new Config();
//        config.useClusterServers();
//        config.useSingleServer().setAddress("redis://localhost:6379");
//        return Redisson.create(config);
//    }
}
