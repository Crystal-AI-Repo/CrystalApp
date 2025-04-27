package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.getPagedData
import com.lovelycatv.ai.crystalapp.entity.ChatCharacterEntity
import com.lovelycatv.ai.crystalapp.mapper.ChatCharacterMapper
import com.lovelycatv.ai.crystalapp.service.ChatCharacterService
import com.lovelycatv.ai.crystalapp.service.ModelService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 22:48
 * @version 1.0
 */
@Service
class ChatCharacterServiceImpl(
    private val modelService: ModelService
) : ChatCharacterService, ServiceImpl<ChatCharacterMapper, ChatCharacterEntity?>() {
    override fun saveOrUpdateCharacter(
        caller: Long,
        characterId: Long?,
        name: String,
        description: String,
        qualifiedModelName: String,
        prompt: String,
        greeting: String,
        privacy: Boolean,
        avatar: String
    ): ServiceFuncResult<*> {
        val modelEntity = modelService.getByQualifiedName(qualifiedModelName)
            ?: return ServiceFuncResult.failed("Model $qualifiedModelName not found")

        if (characterId != null) {
            val exist = getById(characterId) ?: return ServiceFuncResult.failed("Character $characterId not found")
            if (exist.authorUid != caller) {
                return ServiceFuncResult.notResourceOwner()
            }

            return if (updateById(exist.apply {
                this.name = name
                this.description = description
                this.modelId = modelEntity.id!!
                this.maxContextLength = modelEntity.contextLength
                this.prompt = prompt
                this.greetingMessage = greeting
                this.privacy = privacy
                this.avatar = avatar

                this.modifiedTime = System.currentTimeMillis()
            })) {
                ServiceFuncResult.success("Character $name updated successfully")
            } else {
                ServiceFuncResult.failed("Could not update character")
            }
        } else {
            return if (save(
                    ChatCharacterEntity(
                    null,
                    caller,
                    name,
                    description,
                    prompt,
                    greeting,
                    modelEntity.id!!,
                    modelEntity.contextLength,
                    avatar,
                    System.currentTimeMillis(),
                    System.currentTimeMillis(),
                    privacy,
                false
            )
                )) {
                ServiceFuncResult.success("Character $name saved successfully")
            } else {
                ServiceFuncResult.failed("Could not save character")
            }
        }
    }

    /**
     * Delete chat character (mark as deleted)
     *
     * @param userId Requester
     * @param characterId CharacterId
     * @param hardDelete If true, the chat character will be deleted forever and related data should be deleted too
     */
    override fun deleteChatCharacter(userId: Long, characterId: Long, hardDelete: Boolean): ServiceFuncResult<*> {
        return if (hardDelete) {
            throw UnsupportedOperationException("Due to some security reasons, this operation not support yet.")
        } else {
            if (this.updateChatCharacterDeletion(characterId, userId, true)) {
                ServiceFuncResult.success("Character deleted successfully")
            } else {
                ServiceFuncResult.failed("Could not delete character")
            }
        }
    }

    /**
     * Update character delete marker
     *
     * @param characterId CharacterId
     * @param authorUid AuthorUserId, if null, Resource Owner will be not validated
     * @param deleted Is deleted
     * @return Result of updation
     */
    override fun updateChatCharacterDeletion(characterId: Long, authorUid: Long?, deleted: Boolean): Boolean {
        return update(
            UpdateWrapper<ChatCharacterEntity>()
                .set("deleted", deleted)
                .eq("id", characterId)
                .apply {
                    if (authorUid != null) {
                        this.eq("author_uid", authorUid)
                    }
                }
        )
    }

    override fun getUserCreatedCharacters(
        uid: Long,
        page: Long,
        pageSize: Long,
        includingDeleted: Boolean
    ): ServiceFuncResult<PagedData<ChatCharacterEntity>> {
        val pager = Page<ChatCharacterEntity>(page, pageSize)
        val result = page(
            pager,
            QueryWrapper<ChatCharacterEntity>().apply {
                if (!includingDeleted) {
                    this.eq("deleted", false)
                }
                this.eq("author_uid", uid)
                this.orderByDesc("created_time")
            }
        )
        return ServiceFuncResult.success(
            "",
            PagedData(
                total = result.total,
                pages = result.pages,
                current = page,
                records = result.records
            )
        )
    }

    override fun getMostRecentCharacters(page: Long, pageSize: Long): ServiceFuncResult<PagedData<ChatCharacterEntity>> {
        return ServiceFuncResult.success(
            "",
            getPagedData(page, pageSize) {
                this.eq("deleted", false)
                    .eq("privacy", true)
                    .orderByDesc("created_time")
            }
        )
    }
}