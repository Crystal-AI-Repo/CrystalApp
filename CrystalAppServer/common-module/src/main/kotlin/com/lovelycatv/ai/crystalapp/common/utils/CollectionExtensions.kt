package com.lovelycatv.ai.crystalapp.common.utils

/**
 * @author lovelycat
 * @since 2025-04-20 20:30
 * @version 1.0
 */
class CollectionExtensions private constructor()

fun <E> List<E>.startWith(list: List<E>): Boolean {
    if (this.size < list.size) return false
    for (i in list.indices)
        if (this[i] != list[i]) return false
    return true
}