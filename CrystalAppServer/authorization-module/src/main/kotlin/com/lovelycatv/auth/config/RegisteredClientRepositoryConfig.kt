package com.lovelycatv.auth.config

import com.lovelycatv.auth.api.AuthorizationModuleConfigure
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository


/**
 * @author lovelycat
 * @since 2025-04-06 18:47
 * @version 1.0
 */
@Configuration
class RegisteredClientRepositoryConfig(
    private val authorizationModuleImplementations: AuthorizationModuleConfigure
) {
    /**
     * ClientRepository
     *
     * @return [RegisteredClientRepository]
     */
    @Bean
    fun registeredClientRepository(): RegisteredClientRepository {
        return authorizationModuleImplementations.repoConfig.clientRepository
    }
}