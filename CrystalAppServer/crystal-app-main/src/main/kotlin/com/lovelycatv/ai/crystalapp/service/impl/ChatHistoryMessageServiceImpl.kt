package com.lovelycatv.ai.crystalapp.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.common.utils.SnowIdGenerator
import com.lovelycatv.ai.crystalapp.data.AbstractChatMessage
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType
import com.lovelycatv.ai.crystalapp.mapper.ChatHistoryMessageMapper
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageRelationService
import com.lovelycatv.ai.crystalapp.service.ChatHistoryMessageService
import jakarta.annotation.Resource
import org.springframework.stereotype.Service

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
     * Add a header node as the start of a new chat message tree
     *
     * @param senderType Type of whom sent this message, [ChatMessageType]
     * @param senderId   SenderId, could be userId / characterId ...
     * @param message    [AbstractChatMessage]
     * @return Id of this new node
     */
    override fun createMessageTreeHeader(
        senderType: ChatMemberType,
        senderId: Long,
        message: AbstractChatMessage
    ): ServiceFuncResult<Long?> {
        val id = chatHistoryMessageIdGenerator.nextId(0)
        val result = this.save(
            ChatHistoryMessageEntity(
                id = id,
                senderType = senderType.typeId,
                sender = senderId,
                messageType = message.messageType.typeId,
                message = message.originalMessage,
                createdTime = System.currentTimeMillis(),
                revoked = false
            ))

        return if (result) {
            ServiceFuncResult.success("Created", id)
        } else {
            ServiceFuncResult.failedWithData("Could not create message tree header, save failed")
        }
    }
}