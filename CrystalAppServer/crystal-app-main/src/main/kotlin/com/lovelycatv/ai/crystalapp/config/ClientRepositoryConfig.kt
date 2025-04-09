package com.lovelycatv.ai.crystalapp.config

import com.lovelycatv.ai.crystalapp.GlobalConstants
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings
import java.util.*


/**
 * @author lovelycat
 * @since 2025-04-06 18:47
 * @version 1.0
 */
@Configuration
class ClientRepositoryConfig {
    /**
     * ClientRepository
     *
     * @param jdbcTemplate    [JdbcTemplate]
     * @param passwordEncoder [PasswordEncoder]
     * @return [RegisteredClientRepository]
     */
    fun registeredClientRepository(
        jdbcTemplate: JdbcTemplate,
        passwordEncoder: PasswordEncoder
    ): RegisteredClientRepository {
        // http://127.0.0.1:8080/oauth2/authorize?client_id=crystal-app&response_type=code&scope=message.read&redirect_uri=http%3A%2F%2Flocalhost%3A5173%2Fconsent

        val registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("crystal-app")
            .clientSecret(passwordEncoder.encode("crystal-app"))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri(GlobalConstants.CUSTOM_FRONT_CONSENT_CONFIRM_PAGE_URI)
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .build()

        // If you do not want to save into database, use InMemoryRegisteredClientRepository
        val registeredClientRepository = JdbcRegisteredClientRepository(jdbcTemplate)

        val repositoryByClientId = registeredClientRepository.findByClientId(registeredClient.clientId)
        if (repositoryByClientId == null) {
            registeredClientRepository.save(registeredClient)
        }

        return registeredClientRepository
    }
}