package com.example.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Data
//application.yml配置前缀，加载yml文件中自定义配置
@ConfigurationProperties(prefix = "caching")
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String,Object> redisTemplate = new RedisTemplate<>();
        // 设置连接池
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置序列化方式
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(jackson2JsonRedisSerializer);

        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

    // 配置注入，key是缓存名称，value是缓存有效期
    private Map<String,Long> ttlmap;

    /**
     * 自定义redisCacheManager
     * @param redisTemplate
     * @return
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate<String,Object> redisTemplate) {
        RedisCacheWriter redisCacheWriter = RedisCacheWriter.nonLockingRedisCacheWriter(redisTemplate.getConnectionFactory());
        return new RedisCacheManager(
                redisCacheWriter,
                // 默认的redis缓存配置，设置redis过期时间(秒)，0表示不过期
                this.buildRedisCacheConfigurationWithTtl(redisTemplate,0L),
                // 针对每一个cache做个性化缓存配置
                this.getRedisCacheConfigurationMap(redisTemplate));
    }

    //根据传参构建缓存配置
    private RedisCacheConfiguration buildRedisCacheConfigurationWithTtl(RedisTemplate<String,Object> redisTemplate, Long ttl){
        return RedisCacheConfiguration.defaultCacheConfig()
                // 设置序列化
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()))
                .entryTtl(Duration.ofSeconds(ttl));
    }

    // 根据ttlmap的属性装配结果，个性化RedisCacheConfiguration
    private Map<String, RedisCacheConfiguration> getRedisCacheConfigurationMap(RedisTemplate<String,Object> redisTemplate) {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        for(Map.Entry<String, Long> entry : ttlmap.entrySet()){
            String cacheName = entry.getKey();
            Long ttl = entry.getValue();
            redisCacheConfigurationMap.put(cacheName,this.buildRedisCacheConfigurationWithTtl(redisTemplate,ttl));
        }
        return redisCacheConfigurationMap;
    }

}
