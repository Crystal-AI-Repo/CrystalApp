package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.entity.ChatCharacterEntity
import org.springframework.web.multipart.MultipartFile

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
        greeting: String,
        privacy: Boolean,
        avatar: MultipartFile?,
        background: MultipartFile?
    ): ServiceFuncResult<*>

    /**
     * Delete chat character (mark as deleted)
     *
     * @param userId Requester
     * @param characterId CharacterId
     * @param hardDelete If true, the chat character will be deleted forever and related data should be deleted too
     */
    fun deleteChatCharacter(userId: Long, characterId: Long, hardDelete: Boolean): ServiceFuncResult<*>

    /**
     * Update character delete marker
     *
     * @param characterId CharacterId
     * @param authorUid AuthorUserId, if null, Resource Owner will be not validated
     * @param deleted Is deleted
     * @return Result of updation
     */
    fun updateChatCharacterDeletion(characterId: Long, authorUid: Long?, deleted: Boolean): Boolean

    fun getUserCreatedCharacters(uid: Long, page: Long, pageSize: Long = 10, includingDeleted: Boolean): ServiceFuncResult<PagedData<ChatCharacterEntity>>

    fun getMostRecentCharacters(page: Long, pageSize: Long): ServiceFuncResult<PagedData<ChatCharacterEntity>>
}