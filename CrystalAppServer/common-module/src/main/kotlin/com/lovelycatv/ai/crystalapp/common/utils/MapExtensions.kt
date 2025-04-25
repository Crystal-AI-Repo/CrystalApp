package com.lovelycatv.ai.crystalapp.common.utils

/**
 * @author lovelycat
 * @since 2025-04-26 01:55
 * @version 1.0
 */
class MapExtensions private constructor()

@Suppress("UNCHECKED_CAST")
fun <K, V: Any> Map<out K, V?>.filterNotNullValues(): Map<K, V> {
    return this.filterValues { it != null } as Map<K, V>
}
fun <K> Map<out K, Boolean>.filterTrueValues(): Map<K, Boolean> {
    return this.filterValues { it }
}

fun <K> Map<out K, Boolean>.filterFalseValues(): Map<K, Boolean> {
    return this.filterValues { !it }
}