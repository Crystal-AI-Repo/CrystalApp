package com.lovelycatv.auth.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.auth.entity.RolePermissionEntity

/**
 * @author lovelycat
 * @since 2025-04-09 20:00
 * @version 1.0
 */
interface RolePermissionService : IService<RolePermissionEntity?> {
    fun getRolesPermissionIds(roleIds: List<Long>, explicitPermission: Boolean): Map<Long, List<RolePermissionEntity>>
}