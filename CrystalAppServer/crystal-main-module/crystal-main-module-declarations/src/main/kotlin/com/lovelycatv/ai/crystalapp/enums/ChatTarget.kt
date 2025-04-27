package com.lovelycatv.ai.crystalapp.enums

/**
 * @author lovelycat
 * @since 2025-04-09 22:47
 * @version 1.0
 */
enum class ChatTarget(val typeId: Int) {
    CHAT_ROLE(0),
    CHAT_GROUP(1);

    companion object {
        fun getByTypeId(typeId: Int): ChatTarget? = ChatTarget.entries.find { it.typeId == typeId }
    }
}