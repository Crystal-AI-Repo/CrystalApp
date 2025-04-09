package com.lovelycatv.ai.crystalapp.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-09 22:44
 * @version 1.0
 */
@TableName("group_chat_members")
data class GroupMemberEntity(
    @TableId(type = IdType.AUTO)
    @TableField("id")
    var id: Long?,
    @TableField("group_id")
    var groupId: Long,
    @TableField("member_type")
    var memberType: Int,
    @TableField("member_id")
    var memberId: Long
)