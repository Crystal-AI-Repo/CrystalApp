package com.lovelycatv.auth.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.auth.entity.RoleEntity

/**
 * @author lovelycat
 * @since 2025-04-09 19:58
 * @version 1.0
 */
interface RoleService : IService<RoleEntity?> {
    /**
     * Get roles with children
     *
     * @param roleIds Target roleIds
     * @param getPermissions If true, the permissions field in [RoleEntity] will be filled
     * @return List of roles
     */
    fun getRolesTree(roleIds: List<Long>, getPermissions: Boolean): List<RoleEntity>
}