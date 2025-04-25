package com.lovelycatv.ai.crystalapp.common.data.kv

import com.lovelycatv.ai.crystalapp.common.utils.filterTrueValues

/**
 * @author lovelycat
 * @since 2025-04-26 02:40
 * @version 1.0
 */
abstract class KeyValueCacheStore<K, V>(
    private val store: KeyValueStore<K, V>
) : KeyValueStore<K, V> by store {
    open fun getIfNotExist(key: K): V? {
        return this.listIfNotExist(listOf(key))[key]
    }

    abstract fun listIfNotExist(keys: Collection<K>): Map<K, V?>

    open fun valueProcessor(v: V): V {
        return v
    }

    override fun get(key: K): V? {
        return if (this.containsKey(key)) {
            this.store.get(key)
        } else {
            this.getIfNotExist(key)?.also {
                this@KeyValueCacheStore.set(key, it)
            }
        }?.let {
            valueProcessor(it)
        }
    }

    private fun getFromStore(key: K) = this.store.get(key)?.let { this.valueProcessor(it) }

    override fun set(key: K, value: V) {
        this.store.set(key, this.valueProcessor(value))
    }

    override fun batchGet(keys: Collection<K>): Map<K, V?> {
        if (keys.isEmpty()) {
            return emptyMap()
        }

        if (keys.size == 1) {
            val key = keys.iterator().next()
            val hit = this.containsKey(key)
            return if (hit) {
                mapOf(key to this.getFromStore(key))
            } else {
                listIfNotExist(keys).onEach { (t, u) ->
                    if (u != null) {
                        this@KeyValueCacheStore.set(t, u)
                    }
                }
            }
        }

        val keysHitCache = keys.associateWith { this.containsKey(it) }.filterTrueValues().keys
        val keysNotHitCache = keys - keysHitCache

        return keysHitCache.associateWith { this.getFromStore(it) } + mutableMapOf<K, V?>().apply {
            if (keysNotHitCache.isEmpty()) {
                return@apply
            }

            val map = listIfNotExist(keysNotHitCache)

            this.putAll(map)

            map.forEach { (t, u) ->
                if (u != null) {
                    this@KeyValueCacheStore.set(t, u)
                }
            }

            (keys - map.keys).forEach {
                this[it] = null
            }
        }
    }
}