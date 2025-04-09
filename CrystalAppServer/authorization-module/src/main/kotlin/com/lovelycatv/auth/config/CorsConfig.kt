package com.lovelycatv.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter


/**
 * @author lovelycat
 * @since 2025-04-06 22:19
 * @version 1.0
 */
@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(): CorsFilter {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin("http://localhost:6173")
        configuration.allowCredentials = true
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")

        val configurationSource = UrlBasedCorsConfigurationSource()
        configurationSource.registerCorsConfiguration("/**", configuration)

        return CorsFilter(configurationSource)
    }

}