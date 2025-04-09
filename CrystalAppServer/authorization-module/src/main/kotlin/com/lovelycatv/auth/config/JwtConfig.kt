package com.lovelycatv.auth.config

import com.nimbusds.jose.jwk.JWKSet
import com.nimbusds.jose.jwk.RSAKey
import com.nimbusds.jose.jwk.source.ImmutableJWKSet
import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.security.oauth2.jwt.JwtDecoder
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration
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
class JwtConfig {
    /**
     * Jwt Source Configuration
     *
     * @return JWKSource
     */
    @Bean
    fun jwkSource(): ImmutableJWKSet<SecurityContext?> {
        val keyPair = generateRsaKey()
        val publicKey: RSAPublicKey = keyPair.public as RSAPublicKey
        val privateKey: RSAPrivateKey = keyPair.private as RSAPrivateKey
        val rsaKey: RSAKey = RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build()
        val jwkSet = JWKSet(rsaKey)
        return ImmutableJWKSet<SecurityContext?>(jwkSet)
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

}