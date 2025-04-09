package com.lovelycatv.ai.crystalapp.common.data.kv

/**
 * @author lovelycat
 * @since 2025-04-06 21:49
 * @version 1.0
 */
interface ExpiringKeyValueStore<K, V> : KeyValueStore<K, V> {
    fun set(key: K, value: V, expiration: Long)
}