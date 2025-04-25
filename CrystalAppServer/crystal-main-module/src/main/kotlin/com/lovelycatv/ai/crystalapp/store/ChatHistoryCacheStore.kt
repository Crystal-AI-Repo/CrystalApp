package com.lovelycatv.ai.crystalapp.store

import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueCacheStore
import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueStore
import com.lovelycatv.ai.crystalapp.common.service.ICacheService
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity

/**
 * @author lovelycat
 * @since 2025-04-26 02:24
 * @version 1.0
 */
open class ChatHistoryCacheStore(
    store: KeyValueStore<Long, ChatHistoryMessageEntity>,
    private val chatHistoryMessageService: ICacheService<ChatHistoryMessageEntity?>
) : KeyValueCacheStore<Long, ChatHistoryMessageEntity>(store) {
    override fun valueProcessor(v: ChatHistoryMessageEntity): ChatHistoryMessageEntity {
        return v.apply {
            this.clearChildren()
        }
    }

    override fun listIfNotExist(keys: Collection<Long>): Map<Long, ChatHistoryMessageEntity?> {
        return chatHistoryMessageService.originalListByIds(keys)
            .filterNotNull()
            .groupBy { it.id }
            .mapValues {
                if (it.value.isNotEmpty())
                    it.value[0]
                else
                    null
            }
    }
}