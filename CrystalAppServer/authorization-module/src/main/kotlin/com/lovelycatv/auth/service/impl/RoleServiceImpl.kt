package com.lovelycatv.auth.service.impl

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.auth.entity.RoleEntity
import com.lovelycatv.auth.entity.RolePermissionEntity
import com.lovelycatv.auth.mapper.RoleMapper
import com.lovelycatv.auth.service.RolePermissionService
import com.lovelycatv.auth.service.RoleService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 19:58
 * @version 1.0
 */
@Service
class RoleServiceImpl(
    private val rolePermissionService: RolePermissionService
) : RoleService, ServiceImpl<RoleMapper, RoleEntity?>() {
    /**
     * Get roles with children
     *
     * @param roleIds Target roleIds
     * @param getPermissions If true, the permissions field in [RoleEntity] will be filled
     * @return List of roles
     */
    override fun getRolesTree(roleIds: List<Long>, getPermissions: Boolean): List<RoleEntity> {
        val roles = (if (roleIds.isEmpty())
            return emptyList()
        else
            listByIds(roleIds)
        ).filterNotNull().toMutableList()

        val rolePermissionsMap: MutableMap<Long, List<RolePermissionEntity>> = mutableMapOf()
        if (getPermissions) {
            rolePermissionsMap.putAll(rolePermissionService.getRolesPermissionIds(roles.map { it.id }, true))

            roles.forEach { roleEntity ->
                roleEntity.apply {
                    this.permissions.clear()
                    this.permissions.addAll((rolePermissionsMap[roleEntity.id] ?: emptyList()).mapNotNull {
                        it.permission
                    })
                }
            }
        }

        val result = mutableListOf<RoleEntity>()

        val iterator = roles.iterator()

        // Filter all parent roles
        while (iterator.hasNext()) {
            val role = iterator.next()
            if (role.extends == 0L) {
                result.add(role)
                iterator.remove()
            }
        }

        result.forEach {
            findAndApplyChildren(it, roles)
        }

        return result
    }

    private fun findAndApplyChildren(target: RoleEntity, roles: MutableList<RoleEntity>): RoleEntity {
        if (roles.isEmpty()) return target

        val children = mutableListOf<RoleEntity>()

        val iterator = roles.iterator()
        while (iterator.hasNext()) {
            val role = iterator.next()
            if (role.extends == target.id) {
                children.add(role)
                iterator.remove()
            }
        }

        // Find children's children
        children.forEach {
            if (it.extends != 0L) {
                findAndApplyChildren(it, roles)
            }
        }

        return target.apply {
            this.children.clear()
            this.children.addAll(children)
        }
    }
}