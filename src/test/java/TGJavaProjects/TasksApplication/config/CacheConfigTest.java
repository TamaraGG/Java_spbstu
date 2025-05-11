package TGJavaProjects.TasksApplication.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.cache.RedisCacheManagerBuilderCustomizer;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CacheConfigTest {

    private CacheConfig cacheConfig;

    @BeforeEach
    void setUp() {
        cacheConfig = new CacheConfig();
    }

    @Test
    void objectMapper_createsDefaultObjectMapper_withoutSpecificDefaultTyping() {
        ObjectMapper objectMapper = cacheConfig.objectMapper();

        assertNotNull(objectMapper, "Primary ObjectMapper should not be null.");

        assertFalse(objectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "WRITE_DATES_AS_TIMESTAMPS should be disabled for the primary ObjectMapper.");

        PolymorphicTypeValidator ptv = objectMapper.getPolymorphicTypeValidator();

    }

    @Test
    void redisObjectMapperInternal_createsObjectMapperWithDefaultTypingEnabledAsProperty() {
        ObjectMapper redisObjectMapper = cacheConfig.redisObjectMapperInternal();

        assertNotNull(redisObjectMapper, "Redis ObjectMapper should not be null.");
        assertFalse(redisObjectMapper.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS),
                "WRITE_DATES_AS_TIMESTAMPS should be disabled for the Redis ObjectMapper.");

        PolymorphicTypeValidator ptv = redisObjectMapper.getPolymorphicTypeValidator();
        assertNotNull(ptv, "PolymorphicTypeValidator should be configured for Redis ObjectMapper.");
        assertTrue(ptv instanceof LaissezFaireSubTypeValidator,
                "Expected LaissezFaireSubTypeValidator for Redis ObjectMapper, indicating default typing is active.");
    }

    @Test
    void cacheConfiguration_isProperlyConfigured() {
        ObjectMapper configuredRedisObjectMapper = cacheConfig.redisObjectMapperInternal();
        RedisCacheConfiguration redisCacheConfiguration = cacheConfig.cacheConfiguration(configuredRedisObjectMapper);

        assertNotNull(redisCacheConfiguration, "RedisCacheConfiguration bean should not be null.");

        assertEquals(Duration.ofMinutes(10), redisCacheConfiguration.getTtl(),
                "Default TTL should be 10 minutes.");

        assertFalse(redisCacheConfiguration.getAllowCacheNullValues(),
                "Caching of null values should be disabled.");
    }

    @Test
    void redisCacheManagerBuilderCustomizer_createsNonNullCustomizer() {
        RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        RedisCacheManagerBuilderCustomizer customizer = cacheConfig.redisCacheManagerBuilderCustomizer(defaultCacheConfiguration);

        assertNotNull(customizer, "RedisCacheManagerBuilderCustomizer bean should not be null.");
    }
}