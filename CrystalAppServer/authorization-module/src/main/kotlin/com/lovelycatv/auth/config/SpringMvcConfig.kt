package com.lovelycatv.auth.config

import com.lovelycatv.auth.config.inject.AuthPrincipalArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * @author lovelycat
 * @since 2025-04-13 15:56
 * @version 1.0
 */
@Configuration
class SpringMvcConfig : WebMvcConfigurer {
    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(AuthPrincipalArgumentResolver())
    }
}