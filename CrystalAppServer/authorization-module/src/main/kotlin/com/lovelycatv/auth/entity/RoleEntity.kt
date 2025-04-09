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
@TableName("user_roles")
data class RoleEntity(
    @TableId(type = IdType.AUTO)
    var id: Long,
    @TableField("name")
    var name: String,
    @TableField("role")
    var role: String,
    @TableField("description")
    var description: String,
    @TableField("extends")
    var extends: Long
) {
    @TableField(exist = false)
    val permissions: MutableList<PermissionEntity> = mutableListOf()
    @TableField(exist = false)
    val children: MutableList<RoleEntity> = mutableListOf()
}