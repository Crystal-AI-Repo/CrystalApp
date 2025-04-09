package com.lovelycatv.auth.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.auth.entity.UserEntity
import com.lovelycatv.auth.mapper.UserMapper

/**
 * @author lovelycat
 * @since 2025-04-09 19:40
 * @version 1.0
 */
interface UserService : IService<UserEntity?> {
    fun getMapper(): UserMapper
}