package com.lovelycatv.ai.crystalapp.config.auth

import com.lovelycatv.ai.crystalapp.GlobalConstants
import com.lovelycatv.ai.crystalapp.config.CrystalAppSettings
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
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings
import java.time.Duration
import java.util.*


/**
 * @author lovelycat
 * @since 2025-04-06 18:47
 * @version 1.0
 */
@Configuration
class ClientRepositoryConfig(
    private val crystalAppSettings: CrystalAppSettings
) {
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
            .clientId(crystalAppSettings.oauth2.clientId)
            .clientSecret(passwordEncoder.encode(crystalAppSettings.oauth2.clientSecret))
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri(GlobalConstants.getCustomFrontConsentConfirmPageUrl(crystalAppSettings.frontBaseUrl))
            .scope(OidcScopes.OPENID)
            .scope(OidcScopes.PROFILE)
            .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
            .tokenSettings(
                TokenSettings.builder()
                    .accessTokenTimeToLive(Duration.ofDays(14))
                    .refreshTokenTimeToLive(Duration.ofDays(31))
                    .authorizationCodeTimeToLive(Duration.ofMinutes(5))
                    .build()
            )
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