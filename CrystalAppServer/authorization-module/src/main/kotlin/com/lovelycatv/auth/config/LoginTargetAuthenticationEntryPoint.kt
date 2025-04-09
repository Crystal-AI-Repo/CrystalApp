package com.lovelycatv.auth.config

import com.lovelycatv.ai.crystalapp.common.utils.logger
import com.lovelycatv.auth.AuthGlobalConstants
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.DefaultRedirectStrategy
import org.springframework.security.web.RedirectStrategy
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint
import org.springframework.security.web.util.UrlUtils
import org.springframework.util.ObjectUtils
import java.io.IOException
import java.lang.Boolean
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import kotlin.String
import kotlin.Throws

/**
 * @author lovelycat
 * @since 2025-04-06 21:15
 * @version 1.0
 */
class LoginTargetAuthenticationEntryPoint(loginFormUrl: String) : LoginUrlAuthenticationEntryPoint(loginFormUrl) {
    private val logger = logger()

    private val redirectStrategy: RedirectStrategy = DefaultRedirectStrategy()
    @Throws(IOException::class, ServletException::class)
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val loginFormUrl = determineUrlToUseForThisRequest(request, response, authException)

        // If the custom login url is not absolute, do nothing
        if (!UrlUtils.isAbsoluteUrl(loginFormUrl)) {
            super.commence(request, response, authException)
            return
        }

        val requestUrl = request.requestURL
        if (!ObjectUtils.isEmpty(request.queryString)) {
            requestUrl.append("?").append(request.queryString)
        }

        val targetParameter = URLEncoder.encode(requestUrl.toString(), StandardCharsets.UTF_8)
        logger.info("Request from: $requestUrl")
        val sessionId = request.getSession(Boolean.FALSE)?.id
        if (sessionId != null) {
            val targetUrl = loginFormUrl + "?target=" + targetParameter + "&" + AuthGlobalConstants.NONCE_HEADER_NAME + "=" + request.getSession(Boolean.FALSE)?.id
            logger.info("Unauthorized request, redirect to: {}", targetUrl)
            this.redirectStrategy.sendRedirect(request, response, targetUrl)
        } else {
            logger.warn("Could not get sessionId")
            this.redirectStrategy.sendRedirect(request, response, "/error")
        }

    }
}
