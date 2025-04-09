package com.lovelycatv.auth.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.lovelycatv.ai.crystalapp.common.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * @author lovelycat
 * @since 2025-04-06 20:47
 * @version 1.0
 */
class LoginSuccessHandler : AuthenticationSuccessHandler {
    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        response.characterEncoding = StandardCharsets.UTF_8.name()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.writer.write(ObjectMapper().writeValueAsString(Result.success("welcome back!")))
        response.writer.flush()
    }
}
