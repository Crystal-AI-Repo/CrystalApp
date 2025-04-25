package com.lovelycatv.ai.crystalapp.common.service

import java.io.Serializable

/**
 * @author lovelycat
 * @since 2025-04-26 02:58
 * @version 1.0
 */
interface ICacheService<T> {
    fun originalGetById(id: Serializable): T

    fun originalListByIds(id: Collection<Serializable>): List<T>
}