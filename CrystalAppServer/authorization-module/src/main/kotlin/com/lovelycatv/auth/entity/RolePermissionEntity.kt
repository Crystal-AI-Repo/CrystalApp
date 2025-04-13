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
@TableName("role_permission_relations")
data class RolePermissionEntity(
    @TableId(type = IdType.AUTO)
    var id: Long,
    @TableField("role_id")
    var roleId: Long,
    @TableField("permission_id")
    var permissionId: Long
) : DataBaseEntity {
    @TableField(exist = false)
    var permission: PermissionEntity? = null
}