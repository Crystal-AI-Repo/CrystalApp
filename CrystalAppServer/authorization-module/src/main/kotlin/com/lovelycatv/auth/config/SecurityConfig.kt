package com.lovelycatv.auth.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.lovelycatv.ai.crystalapp.common.utils.logger
import com.lovelycatv.auth.AuthGlobalConstants
import com.lovelycatv.auth.annotations.NoAuthorization
import com.lovelycatv.auth.api.AuthorizationModuleConfigure
import com.lovelycatv.auth.entity.UserEntity
import com.lovelycatv.auth.extension.KVSecurityContextRepository
import com.lovelycatv.auth.handler.ConsentAuthenticationFailureHandler
import com.lovelycatv.auth.handler.ConsentAuthorizationResponseHandler
import com.lovelycatv.auth.handler.LoginFailureHandler
import com.lovelycatv.auth.handler.LoginSuccessHandler
import com.lovelycatv.auth.misc.UserEntityMixin
import jakarta.annotation.security.PermitAll
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.jackson2.SecurityJackson2Modules
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService.OAuth2AuthorizationParametersMapper
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService.OAuth2AuthorizationRowMapper
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.filter.CorsFilter
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.util.*
import java.util.stream.Collectors


/**
 * @author lovelycat
 * @since 2025-04-06 18:43
 * @version 1.0
 */
@Configuration
class SecurityConfig(
    private val authorizationModuleImplementations: AuthorizationModuleConfigure,
    private val corsFilter: CorsFilter,
    private val applicationContext: ApplicationContext
) {
    val logger = logger()

    @Bean
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
            it.jwt { jwt ->
                jwt.jwtAuthenticationConverter {
                    JwtAuthenticationToken(it, CustomJwtAuthenticationConverter().convert(it), it.getClaimAsString("sub"))
                }
            }
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

        /**
         * Find out all methods annotated by @PermitAll or @NoAuthorization
         */
        val permitUrls = mutableMapOf<HttpMethod, MutableSet<String>>()
        val fxAddPermitUrls = fun (method: HttpMethod, urls: Set<String>) {
            permitUrls[method] = (permitUrls[method] ?: mutableSetOf()).apply { addAll(urls) }
        }
        val requestMappingHandlerMapping = applicationContext.getBean("requestMappingHandlerMapping") as RequestMappingHandlerMapping
        val handlerMethodMap = requestMappingHandlerMapping.handlerMethods
        handlerMethodMap.forEach { (info, handlerMethod) ->
            if (handlerMethod.hasMethodAnnotation(PermitAll::class.java) || handlerMethod.hasMethodAnnotation(NoAuthorization::class.java)) {
                val urls: MutableSet<String> = HashSet()
                if (info.patternsCondition != null) {
                    urls.addAll(info.patternsCondition?.patterns ?: emptyList())
                }

                if (info.pathPatternsCondition != null) {
                    urls.addAll(info.pathPatternsCondition?.patterns?.map { e -> e.patternString } ?: emptyList())
                }

                info.methodsCondition.methods.forEach { method ->
                    if (method != null) {
                        when (method) {
                            RequestMethod.GET -> fxAddPermitUrls.invoke(HttpMethod.GET, urls)
                            RequestMethod.HEAD -> fxAddPermitUrls.invoke(HttpMethod.HEAD, urls)
                            RequestMethod.POST -> fxAddPermitUrls.invoke(HttpMethod.POST, urls)
                            RequestMethod.PUT -> fxAddPermitUrls.invoke(HttpMethod.PUT, urls)
                            RequestMethod.PATCH -> fxAddPermitUrls.invoke(HttpMethod.PATCH, urls)
                            RequestMethod.DELETE -> fxAddPermitUrls.invoke(HttpMethod.DELETE, urls)
                            RequestMethod.OPTIONS -> fxAddPermitUrls.invoke(HttpMethod.OPTIONS, urls)
                            RequestMethod.TRACE -> fxAddPermitUrls.invoke(HttpMethod.TRACE, urls)
                        }
                    }
                }
            }
        }
        permitUrls.forEach {
            logger.info("These following urls will not be authorized")
            logger.info("${it.key.name()}:")
            it.value.forEach { url ->
                logger.info("  $url")
            }
        }

        http.authorizeHttpRequests { registry ->
            // Permit all static resources
            registry.requestMatchers(
                "/assets/**",
                "/webjars/**",
                "/login",
                *authorizationModuleImplementations.securityConfig.allowedUrls.toTypedArray()
            ).permitAll()

            permitUrls.forEach {
                registry.requestMatchers(it.key, *it.value.toTypedArray()).permitAll()
            }

            registry.anyRequest().authenticated()
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
        val authorizationService = authorizationModuleImplementations.serviceConfig.oAuth2AuthorizationService

        /**
         * fix: The class with xxx is not in the allowlist.
         * https://github.com/spring-projects/spring-security/issues/4370
         */
        if (authorizationService is JdbcOAuth2AuthorizationService) {
            val objectMapper = ObjectMapper()
            val classLoader = JdbcOAuth2AuthorizationService::class.java.classLoader
            val securityModules = SecurityJackson2Modules.getModules(classLoader)
            objectMapper.registerModules(securityModules)
            objectMapper.registerModule(OAuth2AuthorizationServerJackson2Module())
            // Kotlin supports
            objectMapper.registerKotlinModule()
            objectMapper.addMixIn(
                UserEntity::class.java,
                UserEntityMixin::class.java
            )

            val rowMapper = OAuth2AuthorizationRowMapper(authorizationModuleImplementations.repoConfig.clientRepository)
            rowMapper.setObjectMapper(objectMapper)
            authorizationService.setAuthorizationRowMapper(rowMapper)

            val oAuth2AuthorizationParametersMapper = OAuth2AuthorizationParametersMapper()
            oAuth2AuthorizationParametersMapper.setObjectMapper(objectMapper)
            authorizationService.setAuthorizationParametersMapper(oAuth2AuthorizationParametersMapper)
        }

        return authorizationService
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
}