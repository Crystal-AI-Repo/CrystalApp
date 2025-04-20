package com.lovelycatv.ai.crystalapp.config

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder


/**
 * @author lovelycat
 * @since 2025-04-20 23:22
 * @version 1.0
 */
@Configuration
class JacksonConfig {
    @Bean
    fun jacksonCustomizer(): Jackson2ObjectMapperBuilderCustomizer? {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            // Parse all Long type properties to String
            builder.serializerByType(Long::class.java, ToStringSerializer.instance)
            builder.serializerByType(java.lang.Long.TYPE, ToStringSerializer.instance)
        }
    }
}