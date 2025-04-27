package com.lovelycatv.ai.crystalapp.config

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.data.kv.InMemoryKeyValueStore
import com.lovelycatv.ai.crystalapp.common.service.ICacheService
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageRelationEntity
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import com.lovelycatv.ai.crystalapp.store.ChatHistoryCacheStore
import com.lovelycatv.ai.crystalapp.store.ChatHistoryRelationChildrenCacheStore
import com.lovelycatv.ai.crystalapp.store.ChatHistoryRelationParentCacheStore
import com.lovelycatv.ai.crystalapp.store.UserContactCacheStore
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * @author lovelycat
 * @since 2025-04-26 01:42
 * @version 1.0
 */
@Configuration
class CacheStoreConfig(
    private val chatHistoryMessageRelationService: IService<ChatHistoryMessageRelationEntity?>,
    private val chatHistoryMessageService: ICacheService<ChatHistoryMessageEntity?>,
    private val userContactService: ICacheService<UserContactEntity?>
) {
    @Bean
    fun chatHistoryRelationChildrenCacheStore(): ChatHistoryRelationChildrenCacheStore {
        return ChatHistoryRelationChildrenCacheStore(InMemoryKeyValueStore(), chatHistoryMessageRelationService)
    }

    @Bean
    fun chatHistoryRelationParentCacheStore(): ChatHistoryRelationParentCacheStore {
        return ChatHistoryRelationParentCacheStore(InMemoryKeyValueStore(), chatHistoryMessageRelationService)
    }

    @Bean
    fun chatHistoryCacheStore(): ChatHistoryCacheStore {
        return ChatHistoryCacheStore(InMemoryKeyValueStore(), chatHistoryMessageService)
    }

    @Bean
    fun UserContactCacheStore(): UserContactCacheStore {
        return UserContactCacheStore(InMemoryKeyValueStore(), userContactService)
    }
}