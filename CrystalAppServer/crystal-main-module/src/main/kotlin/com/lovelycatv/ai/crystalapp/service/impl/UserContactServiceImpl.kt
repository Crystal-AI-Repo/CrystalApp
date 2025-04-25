package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.autoFitNull
import com.lovelycatv.ai.crystalapp.common.data.message.AbstractPromptMessage
import com.lovelycatv.ai.crystalapp.common.data.message.TextPromptMessage
import com.lovelycatv.ai.crystalapp.common.utils.getOneByColumn
import com.lovelycatv.ai.crystalapp.common.utils.getPagedData
import com.lovelycatv.ai.crystalapp.common.utils.logger
import com.lovelycatv.ai.crystalapp.common.utils.rollbackTransaction
import com.lovelycatv.ai.crystalapp.data.BranchPath
import com.lovelycatv.ai.crystalapp.data.TextChatMessage
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType
import com.lovelycatv.ai.crystalapp.enums.ChatTarget
import com.lovelycatv.ai.crystalapp.mapper.UserContactMapper
import com.lovelycatv.ai.crystalapp.service.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Flux

/**
 * @author lovelycat
 * @since 2025-04-09 22:51
 * @version 1.0
 */
@Service
class UserContactServiceImpl(
    private val chatCharacterService: ChatCharacterService,
    private val chatHistoryMessageService: ChatHistoryMessageService,
    private val chatHistoryMessageRelationService: ChatHistoryMessageRelationService,
    private val openApiChatService: OpenApiChatService,
    private val modelService: ModelService
) : UserContactService, ServiceImpl<UserContactMapper, UserContactEntity?>() {
    private val logger = logger()

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
                    rollbackTransaction()

                    ServiceFuncResult.failed("Could not save this contact")
                }
            } else {
                // Rollback
                rollbackTransaction()

                // If greetingMessage save failed, use the result as return value
                s1
            }
        } else if (existing.deleted) {
            // Contact already exists but marked deleted, remove the marker only
            val updateResult = this.updateContactDeletionMark(existing.id!!, false)

            if (updateResult.success) {
                ServiceFuncResult.success("Enjoy your chat with ${character.name}!")
            } else {
                // Rollback
                rollbackTransaction()

                // If delete mark update failed, use the update result as return value
                updateResult
            }
        } else {
            ServiceFuncResult.success("Character is already exist")
        }
    }

    override fun getByContactIdAndUid(contactId: Long, uid: Long): UserContactEntity? {
        return this.getOneByColumn(
            "user_id" to uid,
            "id" to contactId
        )
    }

    override fun getByUidAndTargetId(uid: Long, type: ChatMemberType, targetId: Long): UserContactEntity? {
        return this.getOneByColumn(
            "user_id" to uid,
            "contact_type" to type.typeId,
            "chat_target_id" to targetId
        )
    }

    override fun updateContactDeletionMark(contactId: Long, deleted: Boolean): ServiceFuncResult<*> {
        val exist = this.getById(contactId) ?: return ServiceFuncResult.failed("Character $contactId not found")
        return if (updateById(exist.apply {
            this.deleted = deleted
        })) {
            ServiceFuncResult.success("Update success")
        } else {
            ServiceFuncResult.failed("Update failed")
        }
    }

    @Transactional
    override suspend fun sendMessage(senderUserId: Long, contactId: Long, message: String, branchPath: BranchPath): ServiceFuncResult<Flux<*>?> {
        val targetContact = this.getByContactIdAndUid(contactId, senderUserId) ?: return ServiceFuncResult.failedWithData("Contact $contactId not found")
        val chatHistoryStartFrom = if (targetContact.chatHistoryStart <= 0) {
            // Chat history not found, create a header
            val headerCreateResult: ServiceFuncResult<Long?> = when (targetContact.getContactTypeEnum()) {
                ChatTarget.CHAT_ROLE -> {
                    val character = chatCharacterService.getById(targetContact.chatTargetId)
                    if (character != null) {
                        chatHistoryMessageService.createMessageTreeHeader(ChatMemberType.CHAT_ROLE, character.id!!, TextChatMessage(character.greetingMessage))
                    } else {
                        ServiceFuncResult.failedWithData("Character ${targetContact.chatTargetId} not found")
                    }
                }
                ChatTarget.CHAT_GROUP -> {
                    chatHistoryMessageService.createMessageTreeHeader(ChatMemberType.CHAT_ROLE, 0, null)
                }
                else -> {
                    val msg = "ContactType [${targetContact.contactType}:${targetContact.getContactTypeEnum()?.name}] not supported yet"
                    log.error(msg)
                    ServiceFuncResult.failedWithData(msg)
                }
            }

            if (!headerCreateResult.success) {
                // Could not create message history header, rollback
                rollbackTransaction()

                // Return with failure message
                return headerCreateResult.autoFitNull()
            } else {
                headerCreateResult.data!!
            }
        } else {
            targetContact.chatHistoryStart
        }

        // Find the target leaf node
        val tree = with(chatHistoryMessageService.getFullMessageHistoryTree(chatHistoryStartFrom)) {
            if (this.success) this.data!! else return this.autoFitNull()
        }

        val historyMessages = tree.getMessageChainByBranchPath(branchPath).filter { it.isAvailable() }
        val leafNode = historyMessages.last()

        val resultOfSaveUserMessage = chatHistoryMessageService.addNewMessage(leafNode.id, ChatMemberType.USER, senderUserId, TextChatMessage(message))

        return if (resultOfSaveUserMessage.success) {
            val userMessageEntity = resultOfSaveUserMessage.data!!

            // Message has been saved into database, call chat completion
            when (targetContact.getContactTypeEnum()) {
                ChatTarget.CHAT_ROLE -> {
                    val character = chatCharacterService.getById(targetContact.chatTargetId)
                    if (character != null) {
                        val model = modelService.getById(character.modelId)
                        if (model != null) {
                            val resultOfSaveAssistantMessage = chatHistoryMessageService.addNewMessage(userMessageEntity.id, ChatMemberType.CHAT_ROLE, character.id!!, TextChatMessage("Server is overload now."))
                            if (resultOfSaveAssistantMessage.success) {
                                val characterPrompts = listOf(TextPromptMessage(AbstractPromptMessage.Role.SYSTEM, character.prompt))
                                val combinedChatHistory = (historyMessages + listOf(userMessageEntity)).mapNotNull {
                                    val role = when (it.getSenderTypeEnum()) {
                                        ChatMemberType.CHAT_ROLE -> AbstractPromptMessage.Role.ASSISTANT
                                        ChatMemberType.USER -> AbstractPromptMessage.Role.USER
                                    }

                                    when (it.getMessageTypeEnum()) {
                                        ChatMessageType.TEXT -> TextPromptMessage(role, it.message)
                                        else -> null
                                    }
                                }

                                val assistantMessageId = resultOfSaveAssistantMessage.data!!.id

                                val flux = openApiChatService.streamChatCompletion(
                                    sessionId = assistantMessageId.toString(),
                                    modelName = model.qualifiedName,
                                    prompts = characterPrompts + combinedChatHistory,
                                    onCompleted = { output, _, _ ->
                                        val t = chatHistoryMessageService.modifyMessage(assistantMessageId, TextChatMessage(output))
                                        if (!t.success) {
                                            logger.error("Could not save assistant message to $assistantMessageId. Reason: ${t.message}, UserInput: $message, AssistantOutput: $output")
                                        }
                                    }
                                )

                                if (flux != null) {
                                    ServiceFuncResult.success(resultOfSaveAssistantMessage.data!!.id.toString(), flux)
                                } else {
                                    // Rollback
                                    rollbackTransaction()

                                    ServiceFuncResult.failedWithData("Could not start chat completions, internal server error")
                                }
                            } else {
                                // Rollback
                                rollbackTransaction()

                                ServiceFuncResult.failedWithData("Could not save pre-assistant message")
                            }
                        } else {
                            // Rollback
                            rollbackTransaction()

                            ServiceFuncResult.failedWithData("Model ${character.modelId} not found")
                        }
                    } else {
                        // Rollback
                        rollbackTransaction()

                        ServiceFuncResult.failedWithData("Character ${targetContact.chatTargetId} not found")
                    }
                }
                // ChatTarget.CHAT_GROUP -> {}
                else -> ServiceFuncResult.failedWithData("Unsupported contact type: ${targetContact.contactType}")
            }
        } else {
            // Rollback
            rollbackTransaction()

            resultOfSaveUserMessage.autoFitNull()
        }
    }

    override fun revokeMessage(userId: Long, contactId: Long, messageId: Long): ServiceFuncResult<*> {
        val validaRoot = this.validateMessageRoot(userId, contactId, messageId)
        return if (validaRoot.success) {
            val result = chatHistoryMessageService.revokeMessage(messageId)
            if (result.success) {
                ServiceFuncResult.success("Message revoked")
            } else {
                result
            }
        } else {
            validaRoot
        }
    }

    override fun validateMessageRoot(userId: Long, contactId: Long, messageId: Long): ServiceFuncResult<*> {
        val targetContact = this.getByContactIdAndUid(contactId, userId) ?: return ServiceFuncResult.failed("Contact $contactId not found")
        val searchRootResult = chatHistoryMessageRelationService.searchUpwardsForRoot(messageId, null)

        return if (searchRootResult.success) {
            val root = searchRootResult.data.last()
            // Validate whether the root node id is equals to the startId in targetContact
            if (targetContact.chatHistoryStart == root.currentId) {
                ServiceFuncResult.success("Success")
            } else {
                logger.warn("User $userId is trying to operate message: $messageId but given a wrong contact: ${targetContact.id}. " +
                    "The contact message history start from ${targetContact.chatHistoryStart} but the root of the given message is ${root.currentId}")
                ServiceFuncResult.failed("Message history validation failed")
            }
        } else {
            searchRootResult
        }
    }
}