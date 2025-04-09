package com.lovelycatv.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystalapp.common.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author lovelycat
 * @since 2025-04-06 20:46
 * @version 1.0
 */
class LoginFailureHandler : AuthenticationFailureHandler {
    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(ObjectMapper().writeValueAsString(Result.unauthorized("username or password error")))
        response.writer.flush()
    }
}
