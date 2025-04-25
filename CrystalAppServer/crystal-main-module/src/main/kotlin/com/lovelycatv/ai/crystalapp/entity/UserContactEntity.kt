package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.lovelycatv.ai.crystalapp.enums.ChatTarget

/**
 * @author lovelycat
 * @since 2025-04-09 22:41
 * @version 1.0
 */
@TableName("user_contacts")
data class UserContactEntity(
    @TableId(type = IdType.AUTO)
    @TableField("id")
    var id: Long?,
    @TableField("user_id")
    var userId: Long,
    @TableField("contact_type")
    var contactType: Int,
    @TableField("chat_target_id")
    var chatTargetId: Long,
    @TableField("chat_history_start")
    var chatHistoryStart: Long,
    @TableField("deleted")
    var deleted: Boolean
) {
    fun toPublicVO() = PublicVO(id!!, contactType, chatTargetId)

    fun getContactTypeEnum() = ChatTarget.getByTypeId(this.contactType)

    data class PublicVO(
        var id: Long,
        var contactType: Int,
        var chatTargetId: Long
    ) {
        fun getContactTypeEnum() = ChatTarget.getByTypeId(this.contactType)
    }
}
