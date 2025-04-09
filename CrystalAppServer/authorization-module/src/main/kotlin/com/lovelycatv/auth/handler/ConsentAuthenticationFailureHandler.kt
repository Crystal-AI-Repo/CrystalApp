package com.lovelycatv.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystalapp.common.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.nio.charset.StandardCharsets

/**
 * @author lovelycat
 * @since 2025-04-08 22:29
 * @version 1.0
 */
class ConsentAuthenticationFailureHandler : AuthenticationFailureHandler {
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val authentication = SecurityContextHolder.getContext().authentication
        val authenticationException = exception as OAuth2AuthenticationException
        val error = authenticationException.error

        val message = if (authentication == null) {
            "Login state expired"
        } else {
            error.toString()
        }

        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(ObjectMapper().writeValueAsString(Result.unauthorized(message)))
        response.writer.flush()
    }
}