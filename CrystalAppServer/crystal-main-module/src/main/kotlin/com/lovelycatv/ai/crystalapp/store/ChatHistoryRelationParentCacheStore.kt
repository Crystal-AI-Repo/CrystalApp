package com.lovelycatv.ai.crystalapp.store

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueCacheStore
import com.lovelycatv.ai.crystalapp.common.data.kv.KeyValueStore
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageRelationEntity

/**
 * @author lovelycat
 * @since 2025-04-26 01:36
 * @version 1.0
 */
open class ChatHistoryRelationParentCacheStore(
    store: KeyValueStore<Long, List<ChatHistoryMessageRelationEntity>>,
    private val chatHistoryMessageRelationService: IService<ChatHistoryMessageRelationEntity?>
) : KeyValueCacheStore<Long, List<ChatHistoryMessageRelationEntity>>(store) {
    override fun listIfNotExist(keys: Collection<Long>): Map<Long, List<ChatHistoryMessageRelationEntity>?> {
        return chatHistoryMessageRelationService.list(
            QueryWrapper<ChatHistoryMessageRelationEntity>().`in`("next_id", *keys.toTypedArray())
        ).filterNotNull().groupBy { it.nextId }
    }
}