package com.api.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Cấu hình Cache với Redis để tối ưu hóa hiệu suất
 * Sử dụng cho các thao tác đọc dữ liệu thường xuyên như restaurants, foods, drivers
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Value("${spring.data.redis.host:localhost}")
    private String redisHost;

    @Value("${spring.data.redis.port:6379}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    /**
     * Cấu hình Redis Connection Factory
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(redisHost);
        config.setPort(redisPort);
        
        // Chỉ set password nếu có
        if (!redisPassword.isEmpty()) {
            config.setPassword(redisPassword);
        }

        return new JedisConnectionFactory(config);
    }

    /**
     * Cấu hình RedisTemplate để thao tác với Redis
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Sử dụng String serializer cho key
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Sử dụng Jackson serializer cho value
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        jackson2JsonRedisSerializer.setObjectMapper(om);

        template.setValueSerializer(jackson2JsonRedisSerializer);
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.setDefaultSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();

        return template;
    }

    /**
     * Cấu hình Cache Manager với các cache khác nhau có TTL riêng
     */
    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30)) // Default TTL 30 phút
                .disableCachingNullValues();

        // Định nghĩa các cache với TTL khác nhau
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Cache cho restaurants - TTL 1 giờ (ít thay đổi)
        cacheConfigurations.put("restaurants", 
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))
                        .disableCachingNullValues());

        // Cache cho foods - TTL 30 phút 
        cacheConfigurations.put("foods", 
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(30))
                        .disableCachingNullValues());

        // Cache cho active drivers - TTL 5 phút (thay đổi thường xuyên)
        cacheConfigurations.put("activeDrivers", 
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(5))
                        .disableCachingNullValues());

        // Cache cho driver locations - TTL 2 phút (cập nhật liên tục)
        cacheConfigurations.put("driverLocations", 
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(2))
                        .disableCachingNullValues());

        // Cache cho vouchers - TTL 1 giờ
        cacheConfigurations.put("vouchers", 
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1))
                        .disableCachingNullValues());

        // Cache cho orders - TTL 10 phút (cho completed orders)
        cacheConfigurations.put("orders", 
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofMinutes(10))
                        .disableCachingNullValues());

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
} 