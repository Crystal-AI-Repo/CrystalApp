package com.lovelycatv.ai.crystalapp.vo

import com.lovelycatv.ai.crystalapp.entity.UserContactEntity

/**
 * @author lovelycat
 * @since 2025-04-14 22:29
 * @version 1.0
 */
data class UserContactVO(
    val contact: UserContactEntity.PublicVO,
    val reifiedContact: Any?
)