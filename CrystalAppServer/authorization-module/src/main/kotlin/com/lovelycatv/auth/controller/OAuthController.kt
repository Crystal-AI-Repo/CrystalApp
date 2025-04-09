package com.lovelycatv.auth.controller

import com.lovelycatv.ai.crystalapp.common.Result
import com.lovelycatv.auth.AuthGlobalConstants
import com.lovelycatv.auth.api.AuthorizationModuleConfigure
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.stereotype.Controller
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriUtils
import java.lang.Boolean
import java.nio.charset.StandardCharsets
import java.security.Principal
import kotlin.Any
import kotlin.RuntimeException
import kotlin.String


/**
 * @author lovelycat
 * @since 2025-04-08 18:51
 * @version 1.0
 */
@Controller
class OAuthController(
    private val registeredClientRepository: RegisteredClientRepository,
    private val authorizationConsentService: OAuth2AuthorizationConsentService,
    private val authorizationModuleConfigure: AuthorizationModuleConfigure
) {

    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()

    @ResponseBody
    @GetMapping("/oauth2/consent/redirect")
    fun consentRedirect(
        session: HttpSession,
        request: HttpServletRequest,
        response: HttpServletResponse,
        @RequestParam(OAuth2ParameterNames.SCOPE) scope: String,
        @RequestParam(OAuth2ParameterNames.STATE) state: String,
        @RequestParam(OAuth2ParameterNames.CLIENT_ID) clientId: String,
        @RequestHeader(name = AuthGlobalConstants.NONCE_HEADER_NAME, required = false) nonceId: String?
    ): Result<*>? {
        val uriBuilder = UriComponentsBuilder
            .fromUriString(authorizationModuleConfigure.securityConfig.customFrontConsentUrl)
            .queryParam(OAuth2ParameterNames.SCOPE, UriUtils.encode(scope, StandardCharsets.UTF_8))
            .queryParam(OAuth2ParameterNames.STATE, UriUtils.encode(state, StandardCharsets.UTF_8))
            .queryParam(OAuth2ParameterNames.CLIENT_ID, clientId)
            .queryParam(AuthGlobalConstants.NONCE_HEADER_NAME, if (ObjectUtils.isEmpty(nonceId)) session.id else nonceId)
        val uriString = uriBuilder.build(Boolean.TRUE).toUriString()
        redirectStrategy.sendRedirect(request, response, uriString)
        return null
    }


    @ResponseBody
    @GetMapping("/oauth2/consent/parameters")
    fun consentParameters(
        principal: Principal,
        @RequestParam(OAuth2ParameterNames.CLIENT_ID) clientId: String,
        @RequestParam(OAuth2ParameterNames.SCOPE) scope: String,
        @RequestParam(OAuth2ParameterNames.STATE) state: String
    ): Result<*> {
        val consentParameters: Map<String, Any> = getConsentParameters(scope, state, clientId, principal)
        return Result.success("", consentParameters)
    }

    private fun getConsentParameters(
        scope: String,
        state: String,
        clientId: String,
        principal: Principal
    ): Map<String, Any> {
        // Remove scopes that were already approved
        val scopesToApprove: MutableSet<String> = HashSet()
        val previouslyApprovedScopes: MutableSet<String> = HashSet()
        val registeredClient: RegisteredClient = this.registeredClientRepository.findByClientId(clientId)
            ?: throw RuntimeException("Client not exist")
        val currentAuthorizationConsent = this.authorizationConsentService.findById(registeredClient.id, principal.name)
        val authorizedScopes: Set<String> = currentAuthorizationConsent?.scopes ?: emptySet()
        for (requestedScope in StringUtils.delimitedListToStringArray(scope, " ")) {
            if (OidcScopes.OPENID == requestedScope) {
                continue
            }
            if (authorizedScopes.contains(requestedScope)) {
                previouslyApprovedScopes.add(requestedScope)
            } else {
                scopesToApprove.add(requestedScope)
            }
        }

        val i18n = authorizationModuleConfigure.authConfig.scopeDescriptions

        val parameters: MutableMap<String, Any> = HashMap(7)
        parameters["clientId"] = registeredClient.clientId
        parameters["clientName"] = registeredClient.clientName
        parameters["state"] = state
        parameters["scopes"] = scopesToApprove.map { i18n.translate(it.replace(".", "#")) }
        parameters["previouslyApprovedScopes"] = previouslyApprovedScopes.map { i18n.translate(it.replace(".", "#")) }
        parameters["principalName"] = principal.name
        parameters["requestURI"] = "/oauth2/authorize"
        return parameters
    }
}