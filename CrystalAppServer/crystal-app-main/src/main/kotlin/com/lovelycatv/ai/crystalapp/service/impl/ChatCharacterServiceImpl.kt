package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.plugins.pagination.Page
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
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
        greeting: String
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

                this.modifiedTime = System.currentTimeMillis()
            })) {
                ServiceFuncResult.success("Character $name updated successfully")
            } else {
                ServiceFuncResult.failed("Could not update character")
            }
        } else {
            return if (save(ChatCharacterEntity(
                    null,
                    caller,
                    name,
                    description,
                    prompt,
                    greeting,
                    modelEntity.id!!,
                    modelEntity.contextLength,
                    "",
                    System.currentTimeMillis(),
                    System.currentTimeMillis())
            )) {
                ServiceFuncResult.success("Character $name saved successfully")
            } else {
                ServiceFuncResult.failed("Could not save character")
            }
        }
    }

    override fun getUserCreatedCharacters(
        uid: Long,
        page: Long,
        pageSize: Long
    ): ServiceFuncResult<PagedData<ChatCharacterEntity>> {
        val pager = Page<ChatCharacterEntity>(page, pageSize)
        val result = page(pager, QueryWrapper<ChatCharacterEntity>().eq("author_uid", uid))
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
        val pager = Page<ChatCharacterEntity>(page, pageSize)
        val result = page(pager, QueryWrapper<ChatCharacterEntity>().orderByDesc("created_time"))
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
}