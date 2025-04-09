package com.lovelycatv.auth.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.auth.entity.RolePermissionEntity
import com.lovelycatv.auth.mapper.RolePermissionMapper
import com.lovelycatv.auth.service.PermissionService
import com.lovelycatv.auth.service.RolePermissionService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 20:00
 * @version 1.0
 */
@Service
class RolePermissionServiceImpl(
    private val permissionService: PermissionService
) : RolePermissionService, ServiceImpl<RolePermissionMapper, RolePermissionEntity?>() {
    override fun getRolesPermissionIds(roleIds: List<Long>, explicitPermission: Boolean): Map<Long, List<RolePermissionEntity>> {
        if (roleIds.isEmpty()) return mapOf()

        val flatRoles = list(QueryWrapper<RolePermissionEntity>().`in`("role_id", *roleIds.toTypedArray())).filterNotNull()
        if (explicitPermission) {
            val permsMap = permissionService.listByIds(flatRoles.map { it.permissionId }).filterNotNull().associateBy { it.id }
            flatRoles.forEach {
                it.apply {
                    this.permission = permsMap[it.permissionId]
                }
            }
        }
        return flatRoles.groupBy { it.roleId }
    }
}