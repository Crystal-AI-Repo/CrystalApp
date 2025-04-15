package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType
import com.lovelycatv.ai.crystalapp.enums.ChatTarget

/**
 * @author lovelycat
 * @since 2025-04-14 20:31
 * @version 1.0
 */
@TableName("chat_history_0")
data class ChatHistoryMessageEntity(
    @TableId
    @TableField("id")
    val id: Long,
    @TableField("sender_type")
    val senderType: Int,
    @TableField("sender")
    val sender: Long,
    @TableField("message_type")
    val messageType: Int,
    @TableField("message")
    val message: String,
    @TableField("created_time")
    val createdTime: Long,
    @TableField("revoked")
    val revoked: Boolean
) {
    fun getSenderTypeEnum() = ChatMemberType.getByTypeId(this.senderType)

    fun getMessageTypeEnum() = ChatMessageType.getByTypeId(this.messageType)
}