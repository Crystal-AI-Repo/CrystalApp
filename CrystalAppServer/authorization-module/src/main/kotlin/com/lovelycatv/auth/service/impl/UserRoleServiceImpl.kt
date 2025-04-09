package com.lovelycatv.auth.service.impl

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
import com.lovelycatv.auth.entity.UserRoleEntity
import com.lovelycatv.auth.mapper.UserRoleMapper
import com.lovelycatv.auth.service.UserRoleService
import org.springframework.stereotype.Service

/**
 * @author lovelycat
 * @since 2025-04-09 20:01
 * @version 1.0
 */
@Service
class UserRoleServiceImpl : UserRoleService, ServiceImpl<UserRoleMapper, UserRoleEntity?>() {
    override fun getUserRoleIds(userId: Long): List<UserRoleEntity> {
        return list(QueryWrapper<UserRoleEntity>().eq("user_id", userId)).filterNotNull()
    }
}