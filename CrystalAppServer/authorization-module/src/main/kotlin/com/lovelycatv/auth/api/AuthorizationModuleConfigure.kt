package com.lovelycatv.auth.api

import com.lovelycatv.ai.crystalapp.common.data.kv.ExpiringKeyValueStore
import com.lovelycatv.ai.crystalapp.common.i18n.I18nMap
import com.lovelycatv.auth.data.ScopeDescription
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository

/**
 * @author lovelycat
 * @since 2025-04-06 22:44
 * @version 1.0
 */
abstract class AuthorizationModuleConfigure {
    val securityConfig by lazy { setSecurityConfiguration() }
    val repoConfig by lazy { setRepositoryConfiguration() }
    val serviceConfig by lazy { setServiceConfiguration() }
    val authConfig by lazy { setAuthorizationConfiguration() }

    protected abstract fun setSecurityConfiguration(): SecurityConfiguration

    protected abstract fun setRepositoryConfiguration(): RepositoryConfiguration

    protected abstract fun setServiceConfiguration(): ServiceConfiguration

    protected abstract fun setAuthorizationConfiguration(): AuthorizationConfiguration

    abstract class SecurityConfiguration {
        val customFrontLoginUrl by lazy { setCustomFrontLoginUrl() }
        val customFrontConsentUrl by lazy { setCustomFrontConsentUrl() }
        val passwordEncoder by lazy { setPasswordEncoder() }

        protected abstract fun setCustomFrontLoginUrl(): String

        protected abstract fun setCustomFrontConsentUrl(): String

        protected abstract fun setPasswordEncoder(): PasswordEncoder
    }

    abstract class RepositoryConfiguration {
        val securityContextStore by lazy { setSecurityContextStore() }
        val clientRepository by lazy { setClientRepository() }

        protected abstract fun setClientRepository(): RegisteredClientRepository

        protected abstract fun setSecurityContextStore(): ExpiringKeyValueStore<String, SecurityContext>
    }

    abstract class ServiceConfiguration {
        val oAuth2AuthorizationService by lazy { setOAuth2AuthorizationService() }
        val oAuth2AuthorizationConsentService by lazy { setOAuth2AuthorizationConsentService() }

        protected abstract fun setOAuth2AuthorizationService(): OAuth2AuthorizationService

        protected abstract fun setOAuth2AuthorizationConsentService(): OAuth2AuthorizationConsentService
    }

    abstract class AuthorizationConfiguration {
        val scopeDescriptions by lazy { setScopeDescriptions() }

        protected abstract fun setScopeDescriptions(): I18nMap<ScopeDescription>
    }
}
