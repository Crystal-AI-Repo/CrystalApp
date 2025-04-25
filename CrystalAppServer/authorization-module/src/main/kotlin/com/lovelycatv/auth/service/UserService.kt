package com.lovelycatv.auth.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.auth.dto.UpdateProfileDTO
import com.lovelycatv.auth.entity.UserEntity
import com.lovelycatv.auth.mapper.UserMapper
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

/**
 * @author lovelycat
 * @since 2025-04-09 19:40
 * @version 1.0
 */
interface UserService : IService<UserEntity?> {
    fun getMapper(): UserMapper

    fun updateProfile(uid: Long, dto: UpdateProfileDTO): ServiceFuncResult<*>

    @Transactional(propagation = Propagation.SUPPORTS)
    fun updateAvatar(uid: Long, resourceId: Long): ServiceFuncResult<*>
}