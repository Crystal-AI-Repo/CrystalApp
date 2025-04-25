package com.lovelycatv.auth.config

import com.lovelycatv.auth.AuthGlobalConstants
import com.lovelycatv.auth.api.AuthorizationModuleConfigure
import com.lovelycatv.auth.entity.UserEntity
import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.stereotype.Component
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.util.*


/**
 * @author lovelycat
 * @since 2025-04-06 18:44
 * @version 1.0
 */
@Component
class JwtConfig(
    private val authorizationModuleConfigure: AuthorizationModuleConfigure
) {
    /**
     * Jwt Source Configuration
     *
     * @return JWKSource
     */
    @Bean
    fun jwkSource(): ImmutableJWKSet<SecurityContext> {
        val store = authorizationModuleConfigure.repoConfig.jwtSourceStore
        val cached = store.get(AuthGlobalConstants.JWK_SOURCE_CACHE_KEY)

        val jwkSet = if (cached.isNullOrBlank()) {
            val keyPair = generateRsaKey()
            val publicKey: RSAPublicKey = keyPair.public as RSAPublicKey
            val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
            val rsaKey: RSAKey = RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build()

            val jwkSet = JWKSet(rsaKey)
            val jwtSetString = jwkSet.toString(false)
            store.set(AuthGlobalConstants.JWK_SOURCE_CACHE_KEY, jwtSetString)

            jwkSet
        } else {
            JWKSet.parse(cached)
        }


        return ImmutableJWKSet<SecurityContext>(jwkSet)
    }

    /**
     * JwtDecoder
     *
     * @param jwkSource JWKSource
     * @return JwtDecoder
     */
    @Bean
    fun jwtDecoder(jwkSource: JWKSource<SecurityContext?>): JwtDecoder {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource)
    }

    /**
     * Generate a random RSA Key
     *
     * @return Random RSA Key
     */
    private fun generateRsaKey(): KeyPair {
        val keyPair = try {
            val keyPairGenerator = KeyPairGenerator.getInstance("RSA")
            keyPairGenerator.initialize(2048)
            keyPairGenerator.generateKeyPair()
        } catch (ex: Exception) {
            throw IllegalStateException(ex)
        }
        return keyPair
    }

    /**
     * Custom jwt payloads
     *
     * @return Instance of [OAuth2TokenCustomizer]
     */
    @Bean
    fun oAuth2TokenCustomizer(): OAuth2TokenCustomizer<JwtEncodingContext> {
        return OAuth2TokenCustomizer<JwtEncodingContext> { context ->
            val principal = context.getPrincipal<Authentication>().principal
            if (principal is UserDetails) {
                val authorities: Collection<GrantedAuthority> = principal.authorities
                val authoritySet = authorities.mapNotNull { it.authority }.toSet()
                val claims = context.claims
                claims.claim("permissions", authoritySet)

                if (principal is UserEntity) {
                    claims.claim("uid", principal.id.toString())
                }
            }
        }
    }

    @Bean
    fun customJwtAuthenticationConverter(): JwtAuthenticationConverter? {
        val converter = JwtAuthenticationConverter()
        converter.setJwtGrantedAuthoritiesConverter(CustomJwtAuthenticationConverter())
        return converter
    }
}