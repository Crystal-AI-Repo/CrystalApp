package com.lovelycatv.ai.crystalapp.config

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.annotation.PropertyAccessor
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import org.springframework.security.jackson2.CoreJackson2Module

/**
 * Redis的key序列化配置类
 *
 * @author vains
 */
@Configuration
class RedisConfig(
    private val builder: Jackson2ObjectMapperBuilder
) {
    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        val stringRedisSerializer = StringRedisSerializer()
        val objectMapper = builder.createXmlMapper(false).build<ObjectMapper>()
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)

        // fix: java.lang.ClassCastException: java.util.LinkedHashMap cannot be cast to XXX
        objectMapper.activateDefaultTyping(
            objectMapper.polymorphicTypeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )

        // Add Jackson Mixin provided by SpringSecurity
        objectMapper.registerModule(CoreJackson2Module())

        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)
        val redisTemplate = RedisTemplate<Any, Any>()

        redisTemplate.valueSerializer = valueSerializer
        redisTemplate.hashValueSerializer = valueSerializer
        redisTemplate.keySerializer = stringRedisSerializer
        redisTemplate.stringSerializer = stringRedisSerializer
        redisTemplate.hashKeySerializer = stringRedisSerializer

        redisTemplate.connectionFactory = connectionFactory
        return redisTemplate
    }

    @Bean
    fun redisHashTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<Any, Any> {
        return redisTemplate(connectionFactory)
    }
}
