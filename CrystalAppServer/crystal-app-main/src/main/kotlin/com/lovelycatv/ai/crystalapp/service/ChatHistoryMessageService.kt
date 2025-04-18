package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.data.AbstractChatMessage
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType
import org.springframework.transaction.annotation.Transactional

/**
 * @author lovelycat
 * @since 2025-04-14 20:37
 * @version 1.0
 */
interface ChatHistoryMessageService : IService<ChatHistoryMessageEntity?> {
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
    fun createMessageTreeHeader(senderType: ChatMemberType, senderId: Long, message: AbstractChatMessage?): ServiceFuncResult<Long?>

    fun getFullMessageHistoryTree(headerId: Long): ServiceFuncResult<ChatHistoryMessageEntity?>

    @Transactional
    fun addNewMessage(parentId: Long, senderType: ChatMemberType, senderId: Long, message: AbstractChatMessage): ServiceFuncResult<Long?>

    fun revokeMessage(messageId: Long): ServiceFuncResult<*>
}