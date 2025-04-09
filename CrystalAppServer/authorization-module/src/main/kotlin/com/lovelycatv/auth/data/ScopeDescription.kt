package com.lovelycatv.auth.data

/**
 * @author lovelycat
 * @since 2025-04-08 20:06
 * @version 1.0
 */
data class ScopeDescription(
    val scopeName: String,
    val description: String
) {
    constructor(pair: Pair<String, String>) : this(pair.first, pair.second)
}