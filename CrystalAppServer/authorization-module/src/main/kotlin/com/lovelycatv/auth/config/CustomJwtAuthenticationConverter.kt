package com.lovelycatv.auth.config

import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import java.util.stream.Collectors
import java.util.stream.Stream

class CustomJwtAuthenticationConverter : Converter<Jwt, Collection<GrantedAuthority>> {
    private val defaultGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
    override fun convert(jwt: Jwt): Collection<GrantedAuthority> {
        val authorities = defaultGrantedAuthoritiesConverter.convert(jwt)

        val customAuthorities = extractCustomAuthorities(jwt)

        return Stream.concat(
            authorities!!.stream(),
            customAuthorities.stream()
        ).collect(Collectors.toSet())
    }

    private fun extractCustomAuthorities(jwt: Jwt): Collection<GrantedAuthority> {
        val authoritiesClaim = jwt.getClaim<Any>("permissions")
            ?: return emptyList()

        return (authoritiesClaim as Collection<String>).stream()
            .map { authority: String? -> SimpleGrantedAuthority(authority) }
            .collect(Collectors.toList())
    }
}