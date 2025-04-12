package com.lovelycatv.auth.utils

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.security.Principal

object PrincipalUtils {

}

fun <T> withPrincipal(principal: Principal, fx: (AuthPrincipal) -> T): T {
    return if (principal is JwtAuthenticationToken) {
        fx.invoke(AuthPrincipal(
            username = principal.name,
            userId = principal.tokenAttributes["uid"] as? Long? ?: throw IllegalStateException("Principal ${principal.name} does not have a valid uid"),
            permissions = principal.authorities.map { it.toString() }
        ))
    } else {
        throw IllegalStateException("Unsupported principal type: ${principal::class.qualifiedName}")
    }
}