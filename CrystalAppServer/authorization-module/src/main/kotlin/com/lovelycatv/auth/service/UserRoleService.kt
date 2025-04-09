package com.lovelycatv.auth.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.auth.entity.UserRoleEntity

/**
 * @author lovelycat
 * @since 2025-04-09 20:00
 * @version 1.0
 */
interface UserRoleService : IService<UserRoleEntity?> {
    fun getUserRoleIds(userId: Long): List<UserRoleEntity>
}