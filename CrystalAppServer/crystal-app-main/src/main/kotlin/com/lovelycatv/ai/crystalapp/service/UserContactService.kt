package com.lovelycatv.ai.crystalapp.service

import com.baomidou.mybatisplus.extension.service.IService
import com.lovelycatv.ai.crystalapp.common.PagedData
import com.lovelycatv.ai.crystalapp.common.ServiceFuncResult
import com.lovelycatv.ai.crystalapp.data.BranchPath
import com.lovelycatv.ai.crystalapp.entity.UserContactEntity
import com.lovelycatv.ai.crystalapp.enums.ChatMemberType
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

    fun getByContactIdAndUid(contactId: Long, uid: Long): UserContactEntity?

    fun getByUidAndTargetId(uid: Long, type: ChatMemberType, targetId: Long): UserContactEntity?

    fun getByUidAndCharacterId(uid: Long, characterId: Long): UserContactEntity? {
        return this.getByUidAndTargetId(uid, ChatMemberType.CHAT_ROLE, characterId)
    }

    fun updateContactDeletionMark(contactId: Long, deleted: Boolean): ServiceFuncResult<*>

    @Transactional
    fun sendMessage(senderUserId: Long, contactId: Long, message: String, branchPath: BranchPath): ServiceFuncResult<*>

    fun revokeMessage(userId: Long, contactId: Long, messageId: Long): ServiceFuncResult<*>

    fun validateMessageRoot(userId: Long, contactId: Long, messageId: Long): ServiceFuncResult<*>

    /**
     * Check if the target contact belongs to user
     *
     * @param userId UserId
     * @param contactId ContactId
     * @return If false, the return value will be null
     */
    fun validateContactOwner(userId: Long, contactId: Long): UserContactEntity? = this.getByContactIdAndUid(contactId, userId)
}