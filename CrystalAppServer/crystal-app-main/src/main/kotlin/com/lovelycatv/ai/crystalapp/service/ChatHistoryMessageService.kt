package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.data.AbstractChatMessage
import com.lovelycatv.ai.crystalapp.entity.ChatHistoryMessageEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType

/**
 * @author lovelycat
 * @since 2025-04-14 20:37
 * @version 1.0
 */
interface ChatHistoryMessageService : IService<ChatHistoryMessageEntity?> {
    /**
     * Add a header node as the start of a new chat message tree
     *
     * @param senderType Type of whom sent this message, [ChatMessageType]
     * @param senderId   SenderId, could be userId / characterId ...
     * @param message    [AbstractChatMessage]
     * @return Id of this new node
     */
    fun createMessageTreeHeader(senderType: ChatMemberType, senderId: Long, message: AbstractChatMessage): ServiceFuncResult<Long?>
}