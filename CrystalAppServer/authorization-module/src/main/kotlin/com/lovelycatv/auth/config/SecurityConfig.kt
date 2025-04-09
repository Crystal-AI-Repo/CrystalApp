package com.lovelycatv.auth.config

import com.lovelycatv.auth.AuthGlobalConstants
import com.lovelycatv.auth.api.AuthorizationModuleConfigure
import com.lovelycatv.auth.extension.KVSecurityContextRepository
import com.lovelycatv.auth.handler.ConsentAuthenticationFailureHandler
import com.lovelycatv.auth.handler.ConsentAuthorizationResponseHandler
import com.lovelycatv.auth.handler.LoginFailureHandler
import com.lovelycatv.auth.handler.LoginSuccessHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.web.filter.CorsFilter


/**
 * @author lovelycat
 * @since 2025-04-06 18:43
 * @version 1.0
 */
@Configuration
class SecurityConfig(
    private val authorizationModuleImplementations: AuthorizationModuleConfigure,
    private val corsFilter: CorsFilter
) { @Bean
    @Throws(Exception::class)
    fun authorizationServerSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http)
        http.getConfigurer(OAuth2AuthorizationServerConfigurer::class.java)
            .oidc(Customizer.withDefaults())
            .authorizationEndpoint {
                it.consentPage("/oauth2/consent/redirect")
                it.errorResponseHandler(ConsentAuthenticationFailureHandler())
                it.authorizationResponseHandler(ConsentAuthorizationResponseHandler(authorizationModuleImplementations.securityConfig.customFrontConsentUrl))
            }

        http.exceptionHandling {
            it.defaultAuthenticationEntryPointFor(
                // Custom unauthorized request processor
                LoginTargetAuthenticationEntryPoint(authorizationModuleImplementations.securityConfig.customFrontLoginUrl),
                MediaTypeRequestMatcher(MediaType.TEXT_HTML)
            )
        }

        // As a resource server
        http.oauth2ResourceServer {
            it.jwt(Customizer.withDefaults())
        }

        http.securityContext {
            it.securityContextRepository(KVSecurityContextRepository(authorizationModuleImplementations.repoConfig.securityContextStore))
        }

        return http.build()
    }

    @Bean
    @Throws(java.lang.Exception::class)
    fun defaultSecurityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.addFilter(corsFilter)
        http.csrf { it.disable() }
        http.cors { it.disable() }

        http.authorizeHttpRequests {
            // Permit all static resources
            it.requestMatchers("/assets/**", "/webjars/**", "/login").permitAll()
                .anyRequest().authenticated()
        }.formLogin { formLogin: FormLoginConfigurer<HttpSecurity?> ->
            formLogin.loginPage(AuthGlobalConstants.CUSTOM_LOGIN_PAGE_URI)
                .successHandler(LoginSuccessHandler())
                .failureHandler(LoginFailureHandler())
        }

        // As a resource server
        http.oauth2ResourceServer {
            it.jwt(Customizer.withDefaults())
        }

        http.exceptionHandling {
            it.defaultAuthenticationEntryPointFor(
                // Custom unauthorized request processor
                LoginTargetAuthenticationEntryPoint(authorizationModuleImplementations.securityConfig.customFrontLoginUrl),
                MediaTypeRequestMatcher(MediaType.TEXT_HTML)
            )
        }

        http.securityContext {
            it.securityContextRepository(KVSecurityContextRepository(authorizationModuleImplementations.repoConfig.securityContextStore))
        }

        return http.build()
    }

    /**
     * Custom [PasswordEncoder]
     *
     * @return [PasswordEncoder]
     */
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return authorizationModuleImplementations.securityConfig.passwordEncoder
    }

    @Bean
    fun authorizationService(): OAuth2AuthorizationService {
        return authorizationModuleImplementations.serviceConfig.oAuth2AuthorizationService
    }

    @Bean
    fun authorizationConsentService(): OAuth2AuthorizationConsentService {
        return authorizationModuleImplementations.serviceConfig.oAuth2AuthorizationConsentService
    }

    /**
     * Add authorization server settings, customize issuer, endpoint... settings
     *
     * @return [AuthorizationServerSettings]
     */
    @Bean
    fun authorizationServerSettings(): AuthorizationServerSettings? {
        return AuthorizationServerSettings.builder().build()
    }

    @Bean
    fun users(passwordEncoder: PasswordEncoder): UserDetailsService {
        val user: UserDetails = User.withUsername("admin")
            .password(passwordEncoder.encode("123456"))
            .roles("admin", "normal")
            .authorities("app", "web")
            .build()
        return InMemoryUserDetailsManager(user)
    }
}