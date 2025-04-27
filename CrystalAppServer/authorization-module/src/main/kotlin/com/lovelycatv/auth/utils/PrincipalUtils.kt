package com.lovelycatv.auth.utils

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import java.security.Principal

object PrincipalUtils {

}

fun <T> withPrincipal(principal: Principal, fx: (AuthPrincipal) -> T): T {
    return if (principal is JwtAuthenticationToken) {
        val uid = principal.tokenAttributes["uid"] as? Long?
            ?: (principal.tokenAttributes["uid"] as? Int?)?.toLong()
            ?: (principal.tokenAttributes["uid"] as? String?)?.toLong()
        fx.invoke(AuthPrincipal(
            username = principal.name,
            userId = uid ?: throw IllegalStateException("Principal ${principal.name} does not have a valid uid, payloads: ${principal.tokenAttributes}"),
            permissions = principal.authorities.map { it.toString() }
        ))
    } else {
        throw IllegalStateException("Unsupported principal type: ${principal::class.qualifiedName}")
    }
}