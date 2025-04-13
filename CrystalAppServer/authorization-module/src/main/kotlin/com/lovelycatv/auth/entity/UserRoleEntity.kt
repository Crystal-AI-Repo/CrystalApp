package com.lovelycatv.auth.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName
import com.lovelycatv.ai.crystalapp.common.data.DataBaseEntity

/**
 * @author lovelycat
 * @since 2025-04-09 19:50
 * @version 1.0
 */
@TableName("user_role_relations")
data class UserRoleEntity(
    @TableId(type = IdType.AUTO)
    var id: Long,
    @TableField("user_id")
    var userId: Long,
    @TableField("role_id")
    var roleId: Long
) : DataBaseEntity