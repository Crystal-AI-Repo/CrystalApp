package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.fasterxml.jackson.annotation.JsonIgnore
import com.lovelycatv.ai.crystalapp.data.ChatHistoryMessage
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
import com.lovelycatv.ai.crystalapp.enums.ChatMessageType

/**
 * @author lovelycat
 * @since 2025-04-14 20:31
 * @version 1.0
 */
@TableName("chat_history_0")
data class ChatHistoryMessageEntity(
    @TableId
    @TableField("id")
    var id: Long,
    @TableField("sender_type")
    var senderType: Int,
    @TableField("sender")
    var sender: Long,
    @TableField("message_type")
    var messageType: Int,
    @TableField("message")
    var message: String,
    @TableField("created_time")
    var createdTime: Long,
    @TableField("revoked")
    var revoked: Boolean
) : ChatHistoryMessage<ChatHistoryMessageEntity> {
    @TableField(exist = false)
    private val _children: MutableList<ChatHistoryMessageEntity> = mutableListOf()
    @JsonIgnore
    @TableField(exist = false)
    override val children: List<ChatHistoryMessageEntity> = this._children

    /**
     * Get the sub-type instance of [ChatHistoryMessage]
     *
     * @return Real instance of [ChatHistoryMessage]
     */
    @JsonIgnore
    override fun getMessageEntity(): ChatHistoryMessageEntity {
        return this
    }

    override fun addChildNode(child: ChatHistoryMessageEntity) {
        this._children.add(child)
    }

    override fun addChildNodes(children: Collection<ChatHistoryMessageEntity>) {
        this._children.addAll(children)
    }

    override fun isLeafNode() = this.children.isEmpty()

    fun getSenderTypeEnum() = ChatMemberType.getByTypeId(this.senderType)
        ?: throw IllegalStateException("Unsupported sender type: ${this.senderType}")

    fun getMessageTypeEnum() = ChatMessageType.getByTypeId(this.messageType)
        ?: throw IllegalStateException("Unsupported message type: ${this.messageType}")
}