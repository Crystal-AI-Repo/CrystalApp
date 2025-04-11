package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-09 22:39
 * @version 1.0
 */
@TableName("chat_roles")
data class ChatRoleEntity(
    @TableId(type = IdType.AUTO)
    @TableField("id")
    var id: Long?,
    @TableField("author_id")
    var authorId: Long,
    @TableField("name")
    var name: String,
    @TableField("description")
    var description: String,
    @TableField("prompt")
    var prompt: String,
    @TableField("greeting_message")
    var greetingMessage: String,
    @TableField("model_id")
    var modelId: Long,
    @TableField("max_context_length")
    var maxContextLength: Int,
    @TableField("avatar")
    var avatar: String,
    @TableField("created_time")
    var createdTime: Long,
    @TableField("modified_time")
    var modifiedTime: Long
)