package com.lovelycatv.ai.crystalapp.config.auth

import com.lovelycatv.ai.crystalapp.GlobalConstants
import com.lovelycatv.ai.crystalapp.common.data.kv.ExpiringKeyValueStore
import com.lovelycatv.ai.crystalapp.common.data.kv.InMemoryKeyValueStore
import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueStore
import com.lovelycatv.ai.crystalapp.common.i18n.I18nMap
import com.lovelycatv.ai.crystalapp.config.CrystalAppSettings
import com.lovelycatv.ai.crystalapp.service.SettingService
import com.lovelycatv.ai.crystalapp.store.DataBaseSettingsKeyValueStore
import com.lovelycatv.auth.api.AuthorizationModuleConfigure
import com.lovelycatv.auth.data.ScopeDescription
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.JdbcOAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.stereotype.Component

/**
 * @author lovelycat
 * @since 2025-04-06 22:47
 * @version 1.0
 */
@Component
class AuthorizationModuleImpls(
    private val jdbcTemplate: JdbcTemplate,
    clientRepositoryConfig: ClientRepositoryConfig,
    private val crystalAppSettings: CrystalAppSettings,
    private val settingService: SettingService
) : AuthorizationModuleConfigure() {
    private val registeredClientRepository = clientRepositoryConfig.registeredClientRepository(jdbcTemplate, this.securityConfig.passwordEncoder)

    override fun setSecurityConfiguration(): SecurityConfiguration {
        return object : SecurityConfiguration() {
            override fun setCorsAllowedOrigins(): List<String> {
                return listOf(
                    crystalAppSettings.frontBaseUrl
                )
            }

            override fun setAllowedUrls(): List<String> {
                return listOf("/auth/token", "/error", "/contact/sendMessage")
            }

            override fun setCustomFrontLoginUrl() = GlobalConstants.getCustomFrontLoginPageUrl(crystalAppSettings.oauth2.authFrontBaseUrl)
            override fun setCustomFrontConsentUrl() = GlobalConstants.getCustomFrontConsentPageUrl(crystalAppSettings.oauth2.authFrontBaseUrl)
            override fun setPasswordEncoder() = BCryptPasswordEncoder()
        }
    }

    override fun setRepositoryConfiguration(): RepositoryConfiguration {
        return object : RepositoryConfiguration() {
            override fun setClientRepository(): RegisteredClientRepository {
                return registeredClientRepository
            }

            override fun setSecurityContextStore(): ExpiringKeyValueStore<String, SecurityContext> {
                return InMemoryKeyValueStore()
            }

            override fun setJwtSourceStore(): KeyValueStore<String, String> {
                return DataBaseSettingsKeyValueStore(settingService)
            }
        }
    }

    override fun setServiceConfiguration(): ServiceConfiguration {
        return object : ServiceConfiguration() {
            override fun setOAuth2AuthorizationService(): OAuth2AuthorizationService {
                // If you do not want to save into database, InMemoryOAuth2AuthorizationService
                return JdbcOAuth2AuthorizationService(jdbcTemplate, registeredClientRepository)
            }

            override fun setOAuth2AuthorizationConsentService(): OAuth2AuthorizationConsentService {
                // If you do not want to save into database, use InMemoryOAuth2AuthorizationConsentService
                return JdbcOAuth2AuthorizationConsentService(jdbcTemplate, registeredClientRepository)
            }

        }
    }

    override fun setAuthorizationConfiguration(): AuthorizationConfiguration {
        return object : AuthorizationConfiguration() {
            override fun setScopeDescriptions(): I18nMap<ScopeDescription> {
                val i18n = I18nMap<ScopeDescription>(listOf("en", "zh-cn")) { "en" }
                val values = mutableListOf(
                    "profile" to "Read your profile"
                )
                i18n.addTranslation("en") {
                    values.forEach {
                        putValue(it.first, ScopeDescription(it.first to it.second))
                    }
                }
                return i18n
            }

        }
    }
}