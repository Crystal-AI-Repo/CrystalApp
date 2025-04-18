package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.transform
import com.lovelycatv.ai.crystalapp.common.utils.SnowIdGenerator
import com.lovelycatv.ai.crystalapp.common.utils.listByIds
import com.lovelycatv.ai.crystalapp.common.utils.transactionRollback
import com.lovelycatv.ai.crystalapp.data.AbstractChatMessage
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType
import com.lovelycatv.ai.crystalapp.mapper.ChatHistoryMessageMapper
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageRelationService
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageService
import jakarta.annotation.Resource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * @author lovelycat
 * @since 2025-04-14 20:37
 * @version 1.0
 */
@Service
class ChatHistoryMessageServiceImpl(
    private val chatHistoryMessageRelationService: ChatHistoryMessageRelationService,
    @Resource(name = "chatHistoryMessageIdGenerator")
    private val chatHistoryMessageIdGenerator: SnowIdGenerator
) : ChatHistoryMessageService, ServiceImpl<ChatHistoryMessageMapper, ChatHistoryMessageEntity?>() {
    /**
     * Add a header node as the start of a new chat message tree.
     * At the same time, if the param [message] is not null,
     * a child node will be created and associated with the header node.
     * If the param [message] is null, only 1 header node will be created.
     *
     * @param senderType Type of whom sent this message, [ChatMessageType]
     * @param senderId   SenderId, could be userId / characterId ...
     * @param message    [AbstractChatMessage]
     * @return Id of this header node
     */
    @Transactional
    override fun createMessageTreeHeader(
        senderType: ChatMemberType,
        senderId: Long,
        message: AbstractChatMessage?
    ): ServiceFuncResult<Long?> {
        val headerId = chatHistoryMessageIdGenerator.nextId(0)
        val messageId = chatHistoryMessageIdGenerator.nextId(0)

        val resultOfSaveMessages = this.saveBatch(listOfNotNull(
            ChatHistoryMessageEntity(
                id = headerId,
                senderType = senderType.typeId,
                sender = 0,
                messageType = ChatMessageType.START.typeId,
                message = "<start>",
                createdTime = System.currentTimeMillis(),
                revoked = false
            ),
            message?.let {
                ChatHistoryMessageEntity(
                    id = messageId,
                    senderType = senderType.typeId,
                    sender = senderId,
                    messageType = it.messageType.typeId,
                    message = it.originalMessage,
                    createdTime = System.currentTimeMillis(),
                    revoked = false
                )
            }
        ))

        val resultOfAddRelation = if (message != null) {
            chatHistoryMessageRelationService.addChildNode(headerId, messageId)
        } else {
            ServiceFuncResult.success("Skipped")
        }

        return if (resultOfSaveMessages && resultOfAddRelation.success) {
            ServiceFuncResult.success("Created", headerId)
        } else {
            // Rollback Transaction
            transactionRollback()

            if (!resultOfAddRelation.success) {
                resultOfAddRelation.transform { null }
            } else {
                ServiceFuncResult.failedWithData("Could not save messages")
            }
        }
    }

    override fun getFullMessageHistoryTree(headerId: Long): ServiceFuncResult<ChatHistoryMessageEntity?> {
        val header = this.getById(headerId) ?: return ServiceFuncResult.failedWithData("Header $headerId not found")
        this.recursiveFindChildrenNodes(header)
        return ServiceFuncResult.success("", header)
    }

    @Transactional
    override fun addNewMessage(
        parentId: Long,
        senderType: ChatMemberType,
        senderId: Long,
        message: AbstractChatMessage
    ): ServiceFuncResult<Long?> {
        val messageId = chatHistoryMessageIdGenerator.nextId(0)

        val resultOfSaveMessage = this.save(
            ChatHistoryMessageEntity(
                id = messageId,
                senderType = senderType.typeId,
                sender = senderId,
                messageType = message.messageType.typeId,
                message = message.originalMessage,
                createdTime = System.currentTimeMillis(),
                revoked = false
            )
        )

        val resultOfAddRelation = chatHistoryMessageRelationService.addChildNode(parentId, messageId)

        return if (resultOfSaveMessage && resultOfAddRelation.success) {
            ServiceFuncResult.success("Created", messageId)
        } else {
            // Rollback Transaction
            transactionRollback()

            if (!resultOfAddRelation.success) {
                resultOfAddRelation.transform { null }
            } else {
                ServiceFuncResult.failedWithData("Could not save messages")
            }
        }
    }

    override fun revokeMessage(messageId: Long): ServiceFuncResult<*> {
        val message = this.getById(messageId) ?: return ServiceFuncResult.failed("Message $messageId not found")

        if (message.getMessageTypeEnum() == ChatMessageType.START) {
            return ServiceFuncResult.failed("Could not revoke the message history start mark")
        }

        return if (this.updateById(message.apply { this.revoked = true })) {
            ServiceFuncResult.success("Success")
        } else {
            ServiceFuncResult.failed("Could not update message revoke status")
        }
    }

    private fun recursiveFindChildrenNodes(parent: ChatHistoryMessageEntity) {
        val children = this.listByIds(
            idColumnName = "id",
            entityId = { it.id },
            characterIds = chatHistoryMessageRelationService.getChildrenNodes(parent.id).map { it.nextId }.toTypedArray()
        ).values.filterNotNull()

        children.forEach {
            if (!it.isLeafNode()) {
                this.recursiveFindChildrenNodes(it)
            }
        }

        parent.addChildNodes(children)
    }
}