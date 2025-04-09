package com.lovelycatv.auth.entity

import com.baomidou.mybatisplus.annotation.IdType
import com.baomidou.mybatisplus.annotation.TableField
import com.baomidou.mybatisplus.annotation.TableId
import com.baomidou.mybatisplus.annotation.TableName

/**
 * @author lovelycat
 * @since 2025-04-09 19:50
 * @version 1.0
 */
@TableName("user_permissions")
data class PermissionEntity(
    @TableId(type = IdType.AUTO)
    var id: Long,
    @TableField("permission")
    var permission: String,
    @TableField("name")
    var name: String,
    @TableField("description")
    var description: String
)