package com.lovelycatv.auth.config

import com.lovelycatv.ai.crystalapp.common.utils.UrlUtils
import com.lovelycatv.ai.crystalapp.common.utils.logger
import com.lovelycatv.auth.api.AuthorizationModuleConfigure
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
class CorsConfig(
    private val authorizationModuleConfigure: AuthorizationModuleConfigure
) {
    val logger = logger()

    @Bean
    fun corsFilter(): CorsFilter {
        val configuration = CorsConfiguration()
        configuration.addAllowedOrigin(UrlUtils.getHostFromUrl(authorizationModuleConfigure.securityConfig.customFrontLoginUrl))
        configuration.addAllowedOrigin(UrlUtils.getHostFromUrl(authorizationModuleConfigure.securityConfig.customFrontLoginUrl))
        authorizationModuleConfigure.securityConfig.corsAllowedOrigins.forEach {
            configuration.addAllowedOrigin(it)
        }
        configuration.allowCredentials = true
        configuration.addAllowedMethod("*")
        configuration.addAllowedHeader("*")

        val configurationSource = UrlBasedCorsConfigurationSource()
        configurationSource.registerCorsConfiguration("/**", configuration)

        logger.info("Allowed Origins: ${configuration.allowedOrigins}")

        return CorsFilter(configurationSource)
    }
}