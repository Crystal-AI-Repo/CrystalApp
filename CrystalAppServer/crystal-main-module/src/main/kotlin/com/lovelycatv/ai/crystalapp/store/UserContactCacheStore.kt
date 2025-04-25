package com.lovelycatv.ai.crystalapp.store

import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueCacheStore
import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueStore
import com.lovelycatv.ai.crystalapp.common.service.ICacheService
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import com.lovelycatv.ai.crystalapp.service.UserContactService

/**
 * @author lovelycat
 * @since 2025-04-26 02:24
 * @version 1.0
 */
open class UserContactCacheStore(
    store: KeyValueStore<Long, UserContactEntity>,
    private val userContactService: ICacheService<UserContactEntity?>
) : KeyValueCacheStore<Long, UserContactEntity>(store) {
    override fun listIfNotExist(keys: Collection<Long>): Map<Long, UserContactEntity?> {
        return userContactService.originalListByIds(keys)
            .filterNotNull()
            .groupBy { it.id!! }
            .mapValues {
                if (it.value.isNotEmpty())
                    it.value[0]
                else
                    null
            }
    }
}