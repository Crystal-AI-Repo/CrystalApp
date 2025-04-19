package com.lovelycatv.ai.crystalapp.common.data.message

/**
 * @author lovelycat
 * @since 2025-04-20 03:44
 * @version 1.0
 */
abstract class AbstractPromptMessage(val messageType: Type, val role: Role) {
    enum class Type {
        TEXT
    }

    enum class Role {
        ASSISTANT,
        USER,
        SYSTEM
    }
}