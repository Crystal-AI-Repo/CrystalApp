package com.lovelycatv.ai.crystalapp.enums

/**
 * @author lovelycat
 * @since 2025-04-09 22:47
 * @version 1.0
 */
enum class ChatMemberType(val typeId: Int) {
    CHAT_ROLE(0),
    USER(0);

    companion object {
        fun getByTypeId(typeId: Int): ChatMemberType? = ChatMemberType.entries.find { it.typeId == typeId }
    }
}