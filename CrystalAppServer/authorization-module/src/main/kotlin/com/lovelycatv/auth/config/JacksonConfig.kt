package com.lovelycatv.auth.config

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lovelycatv.auth.entity.UserEntity
import com.lovelycatv.auth.misc.UserEntityMixin
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module

/**
 * @author lovelycat
 * @since 2025-04-09 21:18
 * @version 1.0
 */
@Configuration
class JacksonConfig {
    @Bean
    fun objectMapper(): ObjectMapper {
        val mapper = ObjectMapper()
        mapper.registerKotlinModule()
        mapper.registerModules(SecurityJackson2Modules.getModules(JdbcOAuth2AuthorizationService::class.java.classLoader))
        mapper.registerModule(OAuth2AuthorizationServerJackson2Module())

        mapper.activateDefaultTyping(
            mapper.polymorphicTypeValidator,
            ObjectMapper.DefaultTyping.NON_FINAL,
            JsonTypeInfo.As.PROPERTY
        )
        mapper.setDefaultTyping(null)

        return mapper
    }
}