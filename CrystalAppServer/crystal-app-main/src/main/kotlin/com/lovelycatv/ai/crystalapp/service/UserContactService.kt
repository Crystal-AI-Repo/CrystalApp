package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import org.springframework.transaction.annotation.Transactional

/**
 * @author lovelycat
 * @since 2025-04-09 22:50
 * @version 1.0
 */
interface UserContactService : IService<UserContactEntity?> {
    fun getUserContactList(uid: Long, page: Long, size: Long, includingDeleted: Boolean): ServiceFuncResult<PagedData<UserContactEntity>>

    @Transactional
    fun addCharacterChat(uid: Long, characterId: Long): ServiceFuncResult<*>

    fun getByUidAndCharacterId(uid: Long, characterId: Long): UserContactEntity?

    fun updateContactDeletionMark(characterId: Long, deleted: Boolean): ServiceFuncResult<*>
}