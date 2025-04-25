package com.lovelycatv.ai.crystalapp.enums

/**
 * @author lovelycat
 * @since 2025-04-09 22:47
 * @version 1.0
 */
enum class ChatMessageType(val typeId: Int) {
    START(0),
    TEXT(1);

    companion object {
        fun getByTypeId(typeId: Int): ChatMessageType? = ChatMessageType.entries.find { it.typeId == typeId }
    }
}