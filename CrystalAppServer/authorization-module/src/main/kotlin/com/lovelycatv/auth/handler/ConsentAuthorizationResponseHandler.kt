package com.lovelycatv.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystalapp.common.Result
import feign.Request
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.core.OAuth2Error
import org.springframework.security.oauth2.core.OAuth2ErrorCodes.INVALID_REQUEST
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationException
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationCodeRequestAuthenticationToken
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.UrlUtils
import org.springframework.util.ObjectUtils
import org.springframework.util.StringUtils
import org.springframework.web.util.UriComponentsBuilder
import org.springframework.web.util.UriUtils
import java.nio.charset.StandardCharsets

/**
 * @author lovelycat
 * @since 2025-04-08 22:33
 * @version 1.0
 */
class ConsentAuthorizationResponseHandler(
    private val consentPageUrl: String
) : AuthenticationSuccessHandler {
    private val redirectStrategy = DefaultRedirectStrategy()

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val redirectUri = this.getAuthorizationResponseUri(authentication)
        if (request.method.equals(Request.HttpMethod.POST.name) && UrlUtils.isAbsoluteUrl(consentPageUrl)) {
            response.characterEncoding = StandardCharsets.UTF_8.name()
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.writer.write(ObjectMapper().writeValueAsString(Result.success("authorized", redirectUri)))
            response.writer.flush()
            return
        }
        this.redirectStrategy.sendRedirect(request, response, redirectUri);
    }

    private fun getAuthorizationResponseUri(authentication: Authentication): String {
        val authorizationCodeRequestAuthentication = authentication as OAuth2AuthorizationCodeRequestAuthenticationToken
        if (ObjectUtils.isEmpty(authorizationCodeRequestAuthentication.redirectUri)) {
            val authorizeUriError = "Redirect uri is not null"
            throw OAuth2AuthorizationCodeRequestAuthenticationException(OAuth2Error(INVALID_REQUEST, authorizeUriError, (null)), authorizationCodeRequestAuthentication)
        }

        if (authorizationCodeRequestAuthentication.authorizationCode == null) {
            val authorizeError = "AuthorizationCode is not null"
            throw OAuth2AuthorizationCodeRequestAuthenticationException(OAuth2Error(INVALID_REQUEST, authorizeError, (null)), authorizationCodeRequestAuthentication)
        }

        val uriBuilder = UriComponentsBuilder
            .fromUriString(authorizationCodeRequestAuthentication.redirectUri ?: "")
            .queryParam(OAuth2ParameterNames.CODE, authorizationCodeRequestAuthentication.authorizationCode?.tokenValue)
        if (StringUtils.hasText(authorizationCodeRequestAuthentication.state)) {
            uriBuilder.queryParam(
                OAuth2ParameterNames.STATE,
                UriUtils.encode(authorizationCodeRequestAuthentication.state ?: "", StandardCharsets.UTF_8))
        }
        return uriBuilder.build(true).toUriString()

    }
}