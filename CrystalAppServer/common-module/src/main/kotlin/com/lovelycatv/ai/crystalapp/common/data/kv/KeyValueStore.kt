package com.lovelycatv.ai.crystalapp.common.data.kv

/**
 * @author lovelycat
 * @since 2025-04-06 21:47
 * @version 1.0
 */
interface KeyValueStore<K, V> {
    fun get(key: K): V?

    fun set(key: K, value: V)

    fun remove(key: K): V?

    fun containsKey(key: K): Boolean
}