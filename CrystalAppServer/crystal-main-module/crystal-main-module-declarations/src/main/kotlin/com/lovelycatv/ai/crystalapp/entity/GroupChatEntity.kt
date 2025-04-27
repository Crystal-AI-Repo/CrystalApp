package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-09 22:42
 * @version 1.0
 */
@TableName("group_chats")
data class GroupChatEntity(
    @TableId(type = IdType.AUTO)
    @TableField("id")
    var id: Long?,
    @TableField("creator")
    var creator: Long,
    @TableField("group_name")
    var groupName: String
)
