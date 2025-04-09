package com.lovelycatv.ai.crystalapp.common.data.kv

import java.util.UUID

/**
 * @author lovelycat
 * @since 2025-04-06 21:50
 * @version 1.0
 */
class InMemoryKeyValueStore<K, V> : ExpiringKeyValueStore<K, V> {
    private val map = mutableMapOf<K, V>()
    private val expirationMap = mutableMapOf<K, Long>()

    override fun set(key: K, value: V, expiration: Long) {
        this.set(key, value)
        this.expirationMap[key] = System.currentTimeMillis() + expiration
    }

    override fun get(key: K): V? {
        this.checkAndRemoveExpiredKey()
        return this.map[key]
    }

    override fun set(key: K, value: V) {
        this.checkAndRemoveExpiredKey()
        this.map[key] = value
    }

    override fun remove(key: K): V? {
        return this.map.remove(key).also {
            if (this.expirationMap.containsKey(key)) {
                this.expirationMap.remove(key)
            }
        }
    }

    override fun containsKey(key: K): Boolean = this.map.containsKey(key)

    private fun checkAndRemoveExpiredKey() {
        if (expirationMap.isEmpty()) {
            return
        }
        val currentTime = System.currentTimeMillis()

        expirationMap.forEach { (k, expiration) ->
            if (currentTime > expiration) {
                expirationMap.remove(k)
            }
        }
    }
}