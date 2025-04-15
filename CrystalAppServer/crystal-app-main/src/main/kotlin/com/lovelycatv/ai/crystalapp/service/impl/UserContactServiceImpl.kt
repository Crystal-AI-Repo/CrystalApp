package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.getOneByColumn
import com.lovelycatv.ai.crystalapp.common.utils.getPagedData
import com.lovelycatv.ai.crystalapp.common.utils.transactionRollback
import com.lovelycatv.ai.crystalapp.data.TextChatMessage
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.mapper.UserContactMapper
import com.lovelycatv.ai.crystalapp.service.ChatCharacterService
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageService
import com.lovelycatv.ai.crystalapp.service.UserContactService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author lovelycat
 * @since 2025-04-09 22:51
 * @version 1.0
 */
@Service
class UserContactServiceImpl(
    private val chatCharacterService: ChatCharacterService,
    private val chatHistoryMessageService: ChatHistoryMessageService
) : UserContactService, ServiceImpl<UserContactMapper, UserContactEntity?>() {
    override fun getUserContactList(uid: Long, page: Long, size: Long, includingDeleted: Boolean): ServiceFuncResult<PagedData<UserContactEntity>> {
        return ServiceFuncResult.success(
            "",
            this.getPagedData(page, size) {
                if (includingDeleted) {
                    this.eq("deleted", true)
                }
                this.eq("user_id", uid)
                this.orderByDesc("chat_history_start")
            }
        )
    }

    @Transactional
    override fun addCharacterChat(uid: Long, characterId: Long): ServiceFuncResult<*> {
        val existing = this.getByUidAndCharacterId(uid, characterId)

        // Search the character
        val character = chatCharacterService.getById(characterId)
            ?: return ServiceFuncResult.failed("Character $characterId not found")

        return if (existing == null) {
            // Insert character's greetingMessage into chat history table as the start of the chat
            val s1 = chatHistoryMessageService.createMessageTreeHeader(ChatMemberType.CHAT_ROLE, character.id!!, TextChatMessage(character.greetingMessage))
            if (s1.success) {
                // Insert contact record
                val s2 = this.save(UserContactEntity(null, uid, ChatMemberType.CHAT_ROLE.typeId, character.id!!, s1.data!!, false))

                if (s2) {
                    ServiceFuncResult.success("Enjoy your chat with ${character.name}!")
                } else {
                    // Rollback
                    transactionRollback()

                    ServiceFuncResult.failed("Could not save this contact")
                }
            } else {
                // Rollback
                transactionRollback()

                // If greetingMessage save failed, use the result as return value
                s1
            }
        } else if (existing.deleted) {
            // Contact already exists but marked deleted, remove the marker only
            val updateResult = this.updateContactDeletionMark(character.id!!, false)

            if (updateResult.success) {
                ServiceFuncResult.success("Enjoy your chat with ${character.name}!")
            } else {
                // Rollback
                transactionRollback()

                // If delete mark update failed, use the update result as return value
                updateResult
            }
        } else {
            ServiceFuncResult.success("Character is already exist")
        }
    }

    override fun getByUidAndCharacterId(uid: Long, characterId: Long): UserContactEntity? {
        return this.getOneByColumn("user_id" to uid, "contact_type" to ChatMemberType.CHAT_ROLE.typeId, "chat_target_id" to characterId)
    }

    override fun updateContactDeletionMark(characterId: Long, deleted: Boolean): ServiceFuncResult<*> {
        val exist = this.getById(characterId) ?: return ServiceFuncResult.failed("Character $characterId not found")
        return if (updateById(exist.apply {
            this.deleted = deleted
        })) {
            ServiceFuncResult.success("Update success")
        } else {
            ServiceFuncResult.failed("Update failed")
        }
    }
}