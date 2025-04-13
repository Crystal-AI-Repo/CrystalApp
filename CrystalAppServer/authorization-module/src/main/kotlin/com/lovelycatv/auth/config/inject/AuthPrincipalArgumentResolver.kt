package com.lovelycatv.auth.config.inject

import com.lovelycatv.auth.utils.AuthPrincipal
import com.lovelycatv.auth.utils.withPrincipal
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

/**
 * @author lovelycat
 * @since 2025-04-13 15:57
 * @version 1.0
 */
class AuthPrincipalArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.parameterType == AuthPrincipal::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val principal = webRequest.userPrincipal ?: throw IllegalStateException("This method has not been authenticated, principal is null")
        return withPrincipal(principal) { it }
    }
}