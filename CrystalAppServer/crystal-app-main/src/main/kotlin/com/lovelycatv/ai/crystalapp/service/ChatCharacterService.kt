package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.entity.ChatCharacterEntity

/**
 * @author lovelycat
 * @since 2025-04-09 22:48
 * @version 1.0
 */
interface ChatCharacterService : IService<ChatCharacterEntity?> {
    fun saveOrUpdateCharacter(
        caller: Long,
        characterId: Long?,
        name: String,
        description: String,
        qualifiedModelName: String,
        prompt: String,
        greeting: String
    ): ServiceFuncResult<*>

    fun getUserCreatedCharacters(uid: Long, page: Long, pageSize: Long = 10): ServiceFuncResult<PagedData<ChatCharacterEntity>>

    fun getMostRecentCharacters(page: Long, pageSize: Long): ServiceFuncResult<PagedData<ChatCharacterEntity>>
}