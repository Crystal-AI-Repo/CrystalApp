package com.lovelycatv.auth.utils

data class AuthPrincipal(
    val username: String,
    val userId: Long,
    val permissions: List<String>
)
